package pro.crypto.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Component;
import pro.crypto.Provider;
import pro.crypto.snapshot.SocialUser;

import java.util.LinkedHashMap;

import static pro.crypto.Provider.FACEBOOK;

@Component
@Slf4j
public class FacebookSocialUserProvider implements SocialUserProvider {

    @Override
    public SocialUser getUser(String accessToken) {
        log.info("Getting social user by facebook access token");
        User user = getFacebookUser(accessToken);
        SocialUser socialUser = toSocialUser(user);
        log.info("Got social user by facebook access token: {user: {}}", socialUser);
        return socialUser;
    }

    @Override
    public Provider getProvider() {
        return FACEBOOK;
    }

    private User getFacebookUser(String accessToken) {
        Facebook facebook = new FacebookTemplate(accessToken);
        String[] fields = {"id", "first_name", "last_name", "email", "picture"};
        return facebook.fetchObject("me", User.class, fields);
    }

    private SocialUser toSocialUser(User user) {
        return SocialUser.builder()
                .socialId(user.getId())
                .name(user.getFirstName())
                .surname(user.getLastName())
                .email(user.getEmail())
                .avatarUrl(extractAvatar(user))
                .build();
    }

    private String extractAvatar(User user) {
        return (String) ((LinkedHashMap) ((LinkedHashMap) user.getExtraData()
                .get("picture"))
                .get("data"))
                .get("url");
    }

}
