package pro.crypto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.proxy.HttpNotificationProxy;
import pro.crypto.service.HttpNotificationService;
import pro.crypto.service.NotificationService;

@Configuration
public class NotificationHttpConfiguration {

    @ConditionalOnMissingBean(NotificationService.class)
    @Import(HttpNotificationService.class)
    @EnableFeignClients(clients = HttpNotificationProxy.class)
    @Configuration
    public static class HttpNotificationServiceConfiguration {
    }

}
