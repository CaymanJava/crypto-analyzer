package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.factory.StrategyFactory;
import pro.crypto.factory.StrategyRequestFactory;
import pro.crypto.factory.StrategyRequestTypeResolver;
import pro.crypto.service.RepositoryStrategyService;
import pro.crypto.service.StrategyService;
import pro.crypto.web.StrategyController;

@Configuration
public class StrategyConfiguration {

    @Configuration
    @ConditionalOnMissingBean(StrategyService.class)
    @Import({RepositoryStrategyService.class, StrategyFactory.class,
            StrategyRequestFactory.class, StrategyRequestTypeResolver.class})
    public static class StrategyServiceConfiguration {
    }

    @ConditionalOnWebApplication
    @Import({StrategyController.class})
    @Configuration
    public static class StrategyWebConfiguration {
    }

}
