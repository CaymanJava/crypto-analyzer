package pro.crypto.access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import pro.crypto.Provider;
import pro.crypto.SocialProperties;
import pro.crypto.SocialProviderProperties;
import pro.crypto.exception.SocialIntegrationException;
import pro.crypto.template.GoogleOAuth2Template;

import javax.annotation.PostConstruct;

import static java.lang.String.format;
import static pro.crypto.Provider.GOOGLE;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleSocialAccessProvider implements SocialAccessProvider {

    private final SocialProperties socialProperties;

    private OAuth2Operations oAuth2Operations;
    private SocialProviderProperties configuration;

    @PostConstruct
    public void init() {
        configuration = socialProperties.getConfiguration(GOOGLE);
        oAuth2Operations = new GoogleOAuth2Template(configuration.getClientId(), configuration.getClientSecret());
    }

    @Override
    public AccessGrant getAccessGrant(String authorizationCode) {
        log.trace("Getting Google access grant for auth code.");
        AccessGrant accessGrant = getAccessGrantAfterExchangeForAccess(authorizationCode);
        log.trace("Exchanged Google authorization code to access grant");
        return accessGrant;
    }

    @Override
    public Provider getProvider() {
        return GOOGLE;
    }

    private AccessGrant getAccessGrantAfterExchangeForAccess(String authorizationCode) {
        try {
            return oAuth2Operations.exchangeForAccess(authorizationCode, configuration.getRedirectUrl(), new LinkedMultiValueMap<>());
        } catch (Exception e) {
            throw new SocialIntegrationException(format("Can't exchange for access: {message: %s}", e.getMessage()));
        }
    }

}
