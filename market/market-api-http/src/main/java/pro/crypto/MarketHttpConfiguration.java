package pro.crypto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.proxy.HttpMarketProxy;
import pro.crypto.service.HttpMarketService;
import pro.crypto.service.HttpMarketSynchronizationService;
import pro.crypto.service.MarketService;
import pro.crypto.service.MarketSynchronizationService;

@Configuration
public class MarketHttpConfiguration {

    @ConditionalOnMissingBean(MarketService.class)
    @Import(HttpMarketService.class)
    @EnableFeignClients(clients = HttpMarketProxy.class)
    @Configuration
    public static class MarketHttpServiceConfiguration {
    }

    @ConditionalOnMissingBean(MarketSynchronizationService.class)
    @Import(HttpMarketSynchronizationService.class)
    @Configuration
    public static class MarketSynchronizationHttpServiceConfiguration {
    }

}
