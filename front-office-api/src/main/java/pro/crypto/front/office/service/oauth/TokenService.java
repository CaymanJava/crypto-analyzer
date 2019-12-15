package pro.crypto.front.office.service.oauth;

import org.springframework.http.ResponseEntity;
import pro.crypto.front.office.api.request.AccessTokenRequest;
import pro.crypto.front.office.api.request.RefreshTokenRequest;
import pro.crypto.front.office.api.request.SocialAccessTokenRequest;
import pro.crypto.front.office.api.response.AccessTokenInfo;
import pro.crypto.request.MemberUpdateRequest;

public interface TokenService {

    AccessTokenInfo getTokenInfo(AccessTokenRequest request);

    AccessTokenInfo refreshToken(RefreshTokenRequest request);

    ResponseEntity getSocialTokenInfo(SocialAccessTokenRequest request);

    AccessTokenInfo completeSocialTokenProcess(Long memberId, MemberUpdateRequest request);

}
