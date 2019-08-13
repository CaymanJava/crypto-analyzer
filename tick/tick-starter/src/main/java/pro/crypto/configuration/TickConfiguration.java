package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.mapper.TickDataMapper;
import pro.crypto.service.RepositoryTickService;
import pro.crypto.service.TickService;
import pro.crypto.web.TickController;

@Configuration
public class TickConfiguration {

    @Configuration
    @ConditionalOnMissingBean(TickService.class)
    @Import({RepositoryTickService.class, TickDataMapper.class})
    public static class TickServiceConfiguration {
    }

    @ConditionalOnWebApplication
    @Import({TickController.class})
    @Configuration
    public static class TickWebConfiguration {
    }

}
