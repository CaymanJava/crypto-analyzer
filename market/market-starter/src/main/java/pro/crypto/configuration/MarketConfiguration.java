package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import pro.crypto.model.Market;
import pro.crypto.repository.MarketRepository;
import pro.crypto.service.MarketMapper;
import pro.crypto.service.MarketService;
import pro.crypto.service.MarketSynchronizationService;
import pro.crypto.service.RepositoryMarketService;
import pro.crypto.service.RepositoryMarketSynchronizationService;
import pro.crypto.web.MarketController;

@Configuration
@EnableAsync
public class MarketConfiguration {

    @Configuration
    @ConditionalOnMissingBean(MarketService.class)
    @Import({RepositoryMarketService.class, MarketMapper.class})
    public static class MarketServiceConfiguration {
    }

    @Configuration
    @ConditionalOnMissingBean(MarketSynchronizationService.class)
    @Import({RepositoryMarketSynchronizationService.class})
    public static class MarketSynchronizationServiceConfiguration {
    }

    @ConditionalOnWebApplication
    @Import({MarketController.class})
    @Configuration
    public static class MarketWebConfiguration {
    }

    @EntityScan(basePackageClasses = Market.class)
    @EnableJpaRepositories(basePackageClasses = {MarketRepository.class})
    @Configuration
    public static class MarketRepositoryConfiguration {
    }

}
