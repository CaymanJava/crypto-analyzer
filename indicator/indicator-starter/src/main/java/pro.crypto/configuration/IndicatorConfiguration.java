package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.factory.IndicatorFactory;
import pro.crypto.factory.IndicatorRequestFactory;
import pro.crypto.factory.IndicatorRequestTypeResolver;
import pro.crypto.factory.JsonParser;
import pro.crypto.service.IndicatorService;
import pro.crypto.service.RepositoryIndicatorService;
import pro.crypto.web.IndicatorController;

@Configuration
public class IndicatorConfiguration {

    @Configuration
    @ConditionalOnMissingBean(IndicatorService.class)
    @Import({RepositoryIndicatorService.class, IndicatorFactory.class,
            IndicatorRequestFactory.class, IndicatorRequestTypeResolver.class,
            JsonParser.class})
    public static class IndicatorServiceConfiguration {
    }

    @ConditionalOnWebApplication
    @Import({IndicatorController.class})
    @Configuration
    public static class IndicatorWebConfiguration {
    }

}
