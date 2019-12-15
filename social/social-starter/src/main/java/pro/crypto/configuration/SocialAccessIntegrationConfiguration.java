package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.SocialProperties;
import pro.crypto.access.FacebookSocialAccessProvider;
import pro.crypto.access.GoogleSocialAccessProvider;
import pro.crypto.service.SocialIntegrationProvider;
import pro.crypto.service.SocialIntegrationService;
import pro.crypto.user.FacebookSocialUserProvider;
import pro.crypto.user.GoogleSocialUserProvider;
import pro.crypto.web.SocialIntegrationController;

@Configuration
@EnableConfigurationProperties({SocialProperties.class})
public class SocialAccessIntegrationConfiguration {

    @Configuration
    @ConditionalOnMissingBean(SocialIntegrationService.class)
    @Import({SocialIntegrationProvider.class})
    public static class SocialIntegrationConfiguration {
    }

    @Configuration
    @Import({GoogleSocialAccessProvider.class, FacebookSocialAccessProvider.class})
    public static class SocialAccessProviderConfiguration {
    }

    @Configuration
    @Import({GoogleSocialUserProvider.class, FacebookSocialUserProvider.class})
    public static class SocialUserProviderConfiguration {
    }

    @ConditionalOnWebApplication
    @Import({SocialIntegrationController.class})
    @Configuration
    public static class SocialWebConfiguration {
    }

}
