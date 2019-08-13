package pro.crypto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.proxy.HttpIndicatorProxy;
import pro.crypto.service.HttpIndicatorService;
import pro.crypto.service.IndicatorService;

@Configuration
public class IndicatorHttpConfiguration {

    @ConditionalOnMissingBean(IndicatorService.class)
    @Import(HttpIndicatorService.class)
    @EnableFeignClients(clients = HttpIndicatorProxy.class)
    @Configuration
    public static class IndicatorHttpServiceConfiguration {
    }

}
