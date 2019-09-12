package pro.crypto.front.office.service.oauth;

import pro.crypto.front.office.api.request.AccessTokenRequest;
import pro.crypto.front.office.api.request.RefreshTokenRequest;
import pro.crypto.front.office.api.response.AccessTokenInfo;

public interface TokenService {

    AccessTokenInfo getTokenInfo(AccessTokenRequest request);

    AccessTokenInfo refreshToken(RefreshTokenRequest request);

}
