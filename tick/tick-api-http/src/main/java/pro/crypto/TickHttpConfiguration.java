package pro.crypto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.proxy.HttpTickProxy;
import pro.crypto.service.HttpTickService;
import pro.crypto.service.TickService;

@Configuration
public class TickHttpConfiguration {

    @ConditionalOnMissingBean(TickService.class)
    @Import(HttpTickService.class)
    @EnableFeignClients(clients = HttpTickProxy.class)
    @Configuration
    public static class TickHttpServiceConfiguration {
    }

}
