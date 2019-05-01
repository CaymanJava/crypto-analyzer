package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import pro.crypto.model.Market;
import pro.crypto.repository.MarketRepository;

@AutoConfigureBefore(JpaRepositoriesAutoConfiguration.class)
@ComponentScan(value = {"pro.crypto.service"})
@Configuration
@EnableAsync
public class MarketConfiguration {

    @EntityScan(basePackageClasses = {Market.class})
    @EnableJpaRepositories(basePackageClasses = {MarketRepository.class})
    @Configuration
    public static class MarketRepositoryConfiguration {
    }

}
