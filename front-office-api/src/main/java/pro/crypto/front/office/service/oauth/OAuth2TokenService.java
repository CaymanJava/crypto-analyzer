package pro.crypto.front.office.service.oauth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pro.crypto.front.office.api.exception.InvalidCredentialsException;
import pro.crypto.front.office.api.request.AccessTokenRequest;
import pro.crypto.front.office.api.request.RefreshTokenRequest;
import pro.crypto.front.office.api.request.SocialAccessTokenRequest;
import pro.crypto.front.office.api.response.AccessTokenInfo;
import pro.crypto.request.MemberUpdateRequest;
import pro.crypto.request.SocialUserAccessRequest;
import pro.crypto.service.MemberService;
import pro.crypto.service.SocialIntegrationService;
import pro.crypto.snapshot.SocialAccessSnapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static pro.crypto.MemberStatus.ACTIVE;

@Slf4j
@Service
@AllArgsConstructor
public class OAuth2TokenService implements TokenService {

    private final RestTemplate restTemplate;
    private final MemberService memberService;
    private final ResourceServerProperties resourceServerProperties;
    private final SocialIntegrationService socialIntegrationService;
    private final AuthorizationServerEndpointsConfiguration configuration;

    @Override
    public AccessTokenInfo getTokenInfo(AccessTokenRequest request) {
        log.trace("Getting access token {email: {}}", request.getEmail());
        MultiValueMap<String, String> vars = buildLoginMap(request);
        HttpHeaders headers = buildHeaders();
        ResponseEntity<Map> responseEntity = restTemplate.exchange(resourceServerProperties.getTokenInfoUri(), HttpMethod.POST, new HttpEntity<>(vars, headers), Map.class);
        return handleAccessTokenResponse(responseEntity, request.getEmail());
    }

    @Override
    public AccessTokenInfo refreshToken(RefreshTokenRequest request) {
        log.trace("Refreshing token {request: {}}", request);
        MultiValueMap<String, String> vars = buildRefreshMap(request);
        HttpHeaders headers = buildHeaders();
        ResponseEntity<Map> responseEntity = restTemplate.exchange(resourceServerProperties.getTokenInfoUri(), HttpMethod.POST, new HttpEntity<>(vars, headers), Map.class);
        return handleRefreshResponse(responseEntity);
    }

    @Override
    public ResponseEntity getSocialTokenInfo(SocialAccessTokenRequest request) {
        log.trace("Getting social access token {provider: {}}", request.getProvider());
        SocialAccessSnapshot socialAccessSnapshot = socialIntegrationService.processSocialAccess(toSocialUserAccessRequest(request));
        log.info("Got social access token {socialAccessSnapshot: {}}", socialAccessSnapshot);
        return socialAccessSnapshot.isRegistrationCompleted()
                ? ResponseEntity.ok(getTokenInfoByMemberId(socialAccessSnapshot.getMemberId()))
                : ResponseEntity.ok(socialAccessSnapshot);
    }

    @Override
    public AccessTokenInfo completeSocialTokenProcess(Long memberId, MemberUpdateRequest request) {
        log.trace("Completing social token process {memberId: {}, request: {}}", memberId, request);
        if (request.dataCompleted()) {
            request.setStatus(ACTIVE);
        }
        memberService.update(memberId, request);
        log.info("Completed social token process {memberId: {}, request: {}}", memberId, request);
        return getTokenInfoByMemberId(memberId);
    }

    private AccessTokenInfo getTokenInfoByMemberId(Long memberId) {
        OAuth2Request oAuth2Request = new OAuth2Request(new HashMap<>(), resourceServerProperties.getClientId(), new HashSet<>(), true, new HashSet<>(),
                newHashSet(resourceServerProperties.getResourceId()), null, new HashSet<>(), new HashMap<>());
        Authentication authentication = new UsernamePasswordAuthenticationToken(mapMemberIdToAuthorizedUser(memberId), null, emptyList());
        OAuth2Authentication auth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
        AuthorizationServerTokenServices tokenService = configuration.getEndpointsConfigurer().getTokenServices();
        OAuth2AccessToken token = tokenService.createAccessToken(auth2Authentication);
        return buildAccessTokenInfo(token);
    }

    private AccessTokenInfo buildAccessTokenInfo(OAuth2AccessToken token) {
        return AccessTokenInfo.builder()
                .accessToken(token.getValue())
                .tokenType(token.getTokenType())
                .expiresIn(token.getExpiresIn())
                .refreshToken(token.getRefreshToken().getValue())
                .build();
    }

    private UserDetails mapMemberIdToAuthorizedUser(Long memberId) {
        return new User(memberId.toString(), "", true, true, true, true, emptyList());
    }

    private SocialUserAccessRequest toSocialUserAccessRequest(SocialAccessTokenRequest request) {
        return SocialUserAccessRequest.builder()
                .authCode(request.getAuthCode())
                .provider(request.getProvider())
                .build();
    }

    private MultiValueMap<String, String> buildLoginMap(AccessTokenRequest tokenInfoRequest) {
        MultiValueMap<String, String> vars = new LinkedMultiValueMap<>();
        vars.put("grant_type", singletonList("password"));
        vars.put("username", singletonList(tokenInfoRequest.getEmail()));
        vars.put("password", singletonList(tokenInfoRequest.getPassword()));
        return vars;
    }

    private AccessTokenInfo handleAccessTokenResponse(ResponseEntity<Map> responseEntity, String username) {
        if (isNull(responseEntity) || isNull(responseEntity.getBody())) {
            return handleEmptyResponse(username);
        }

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Got access token: {username: {}}}", username);
            return extractAccessToken(responseEntity.getBody());
        }

        log.warn("Unable to log user {response: {}}", responseEntity.getBody());
        throw new InvalidCredentialsException(format("Unable to log user in: {username: %s}", username));
    }

    private AccessTokenInfo handleEmptyResponse(String username) {
        String errorMessage = format("Unable to log user in: {username: %s}", username);
        log.warn(errorMessage);
        throw new InvalidCredentialsException(errorMessage);
    }

    private AccessTokenInfo handleRefreshResponse(ResponseEntity<Map> responseEntity) {
        if (isNull(responseEntity) || isNull(responseEntity.getBody())) {
            throw new InvalidCredentialsException("Unable to refresh token. Response body is empty");
        }

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Successful token refresh");
            return extractAccessToken(responseEntity.getBody());
        }

        log.warn("Unable to refresh token {response: {}}", responseEntity.getBody());
        throw new InvalidCredentialsException("Unable to refresh token");
    }

    private AccessTokenInfo extractAccessToken(Map body) {
        return AccessTokenInfo.builder()
                .accessToken((String) body.get("access_token"))
                .refreshToken((String) body.get("refresh_token"))
                .tokenType((String) body.get("token_type"))
                .expiresIn((Integer) body.get("expires_in"))
                .build();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private MultiValueMap<String, String> buildRefreshMap(RefreshTokenRequest refreshTokenRequest) {
        MultiValueMap<String, String> vars = new LinkedMultiValueMap<>();
        vars.add("refresh_token", refreshTokenRequest.getRefreshToken());
        vars.add("grant_type", "refresh_token");
        return vars;
    }

}
