package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.properties.MailProperties;
import pro.crypto.properties.MessageRetryConfig;
import pro.crypto.service.MailProviderService;
import pro.crypto.service.MailSenderHandler;
import pro.crypto.service.MailService;
import pro.crypto.service.NotificationService;
import pro.crypto.service.RepositoryNotificationService;
import pro.crypto.service.SmsProviderService;
import pro.crypto.service.SmsService;
import pro.crypto.web.NotificationController;

@Configuration
@EnableConfigurationProperties({MailProperties.class, MessageRetryConfig.class})
public class NotificationConfiguration {

    @Configuration
    @ConditionalOnMissingBean(MailService.class)
    @Import({MailProviderService.class, MailSenderHandler.class})
    public static class MailServiceConfiguration {
    }

    @Configuration
    @ConditionalOnMissingBean(SmsService.class)
    @Import({SmsProviderService.class})
    public static class SmsServiceConfiguration {
    }

    @Configuration
    @ConditionalOnMissingBean(NotificationService.class)
    @Import({RepositoryNotificationService.class})
    public static class NotificationServiceConfiguration {
    }

    @ConditionalOnWebApplication
    @Import({NotificationController.class})
    @Configuration
    public static class NotificationWebConfiguration {
    }

}
