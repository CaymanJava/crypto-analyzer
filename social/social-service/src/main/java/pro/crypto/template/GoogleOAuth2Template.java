package pro.crypto.template;

import org.springframework.social.oauth2.OAuth2Template;

public class GoogleOAuth2Template extends OAuth2Template {

    public GoogleOAuth2Template(String clientId, String clientSecret) {
        super(clientId, clientSecret,
                "https://accounts.google.com/o/oauth2/auth",
                "https://accounts.google.com/o/oauth2/token");
        setUseParametersForClientAuthentication(true);
    }

}
