package pro.crypto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.proxy.SettingProxy;
import pro.crypto.service.HttpSettingService;
import pro.crypto.service.SettingService;

@Configuration
public class HttpSettingConfiguration {

    @ConditionalOnMissingBean(SettingService.class)
    @Import(HttpSettingService.class)
    @EnableFeignClients(clients = SettingProxy.class)
    @Configuration
    public static class HttpSettingServiceConfiguration {
    }

}
