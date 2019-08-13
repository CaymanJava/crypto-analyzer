package pro.crypto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.proxy.HttpStrategyProxy;
import pro.crypto.service.HttpStrategyService;
import pro.crypto.service.StrategyService;

@Configuration
public class StrategyHttpConfiguration {

    @ConditionalOnMissingBean(StrategyService.class)
    @Import(HttpStrategyService.class)
    @EnableFeignClients(clients = HttpStrategyProxy.class)
    @Configuration
    public static class StrategyHttpServiceConfiguration {
    }

}
