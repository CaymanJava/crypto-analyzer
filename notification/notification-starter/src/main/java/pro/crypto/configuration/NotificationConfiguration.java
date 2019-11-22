package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.properties.MailProperties;
import pro.crypto.properties.MessageRetryConfig;
import pro.crypto.properties.SmsProperties;
import pro.crypto.service.MailService;
import pro.crypto.service.NotificationService;
import pro.crypto.service.SmsService;
import pro.crypto.service.mail.MailProviderService;
import pro.crypto.service.mail.MailSenderHandler;
import pro.crypto.service.notification.RepositoryNotificationService;
import pro.crypto.service.sms.AlphaSmsClient;
import pro.crypto.service.sms.SmsProviderService;
import pro.crypto.web.NotificationController;

@Configuration
@EnableConfigurationProperties({MailProperties.class, SmsProperties.class, MessageRetryConfig.class})
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
    @EnableFeignClients(clients = {AlphaSmsClient.class})
    public static class SmsClientsConfiguration {
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
