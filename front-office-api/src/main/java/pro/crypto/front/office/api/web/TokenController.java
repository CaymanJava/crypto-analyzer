package pro.crypto.front.office.api.web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.front.office.api.request.AccessTokenRequest;
import pro.crypto.front.office.api.request.RefreshTokenRequest;
import pro.crypto.front.office.api.response.AccessTokenInfo;
import pro.crypto.front.office.service.oauth.OAuth2TokenService;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = "/tokens")
@AllArgsConstructor
public class TokenController {

    private final OAuth2TokenService oAuthTokenService;

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public AccessTokenInfo getTokenInfo(@Valid @RequestBody AccessTokenRequest accessTokenRequest) {
        return oAuthTokenService.getTokenInfo(accessTokenRequest);
    }

    @PostMapping(value = "/refresh", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public AccessTokenInfo refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return oAuthTokenService.refreshToken(refreshTokenRequest);
    }

}
