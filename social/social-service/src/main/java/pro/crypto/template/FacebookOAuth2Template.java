package pro.crypto.template;

import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.web.client.RestTemplate;

public class FacebookOAuth2Template extends OAuth2Template {

    public FacebookOAuth2Template(String clientId, String clientSecret) {
        super(clientId, clientSecret,
                "https://www.facebook.com/v4.0/dialog/oauth",
                "https://graph.facebook.com/v4.0/oauth/access_token");
        setUseParametersForClientAuthentication(true);
    }

    @Override
    public RestTemplate getRestTemplate() {
        return super.getRestTemplate();
    }

}
