package pro.crypto.front.office.service.oauth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pro.crypto.front.office.api.exception.InvalidCredentialsException;
import pro.crypto.front.office.api.request.AccessTokenRequest;
import pro.crypto.front.office.api.request.RefreshTokenRequest;
import pro.crypto.front.office.api.response.AccessTokenInfo;

import java.util.Map;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;

@Slf4j
@Service
@AllArgsConstructor
public class OAuth2TokenService implements TokenService {

    private final RestTemplate restTemplate;
    private final ResourceServerProperties resourceServerProperties;

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
