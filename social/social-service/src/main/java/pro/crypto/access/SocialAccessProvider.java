package pro.crypto.access;

import org.springframework.social.oauth2.AccessGrant;
import pro.crypto.Provider;

public interface SocialAccessProvider {

    AccessGrant getAccessGrant(String authorizationCode);

    Provider getProvider();

}
