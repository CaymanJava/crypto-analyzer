package pro.crypto.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.oauth2.UserInfo;
import org.springframework.stereotype.Component;
import pro.crypto.Provider;
import pro.crypto.snapshot.SocialUser;

import static pro.crypto.Provider.GOOGLE;

@Component
@Slf4j
public class GoogleSocialUserProvider implements SocialUserProvider {

    @Override
    public SocialUser getUser(String accessToken) {
        log.trace("Getting social user by Google access token");
        Google google = new GoogleTemplate(accessToken);
        UserInfo userInfo = google.oauth2Operations().getUserinfo();
        SocialUser socialUser = toSocialUser(userInfo);
        log.info("Got social user by Google access token: {user: {}}", socialUser);
        return socialUser;
    }

    @Override
    public Provider getProvider() {
        return GOOGLE;
    }

    private SocialUser toSocialUser(UserInfo userInfo) {
        return SocialUser.builder()
                .socialId(userInfo.getId())
                .name(userInfo.getGivenName())
                .surname(userInfo.getFamilyName())
                .email(userInfo.getEmail())
                .avatarUrl(userInfo.getPicture())
                .build();
    }

}
