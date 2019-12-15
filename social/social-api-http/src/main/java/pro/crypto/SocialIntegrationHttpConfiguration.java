package pro.crypto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.proxy.SocialIntegrationProxy;
import pro.crypto.service.HttpSocialIntegrationService;
import pro.crypto.service.SocialIntegrationService;

@Configuration
public class SocialIntegrationHttpConfiguration {

    @ConditionalOnMissingBean(SocialIntegrationService.class)
    @Import(HttpSocialIntegrationService.class)
    @EnableFeignClients(clients = SocialIntegrationProxy.class)
    @Configuration
    public static class HttpSocialIntegrationServiceConfiguration {
    }

}
