package pro.crypto.user;

import pro.crypto.Provider;
import pro.crypto.snapshot.SocialUser;

public interface SocialUserProvider {

    SocialUser getUser(String accessToken);

    Provider getProvider();

}
