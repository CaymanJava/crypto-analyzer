package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.service.MonitoringProviderService;
import pro.crypto.service.MonitoringService;
import pro.crypto.service.RepositorySignalService;
import pro.crypto.service.SignalMapper;
import pro.crypto.service.SignalService;
import pro.crypto.web.MonitoringController;
import pro.crypto.web.SignalController;

@Configuration
public class MonitoringConfiguration {

    @Configuration
    @ConditionalOnMissingBean(SignalService.class)
    @Import({RepositorySignalService.class, SignalMapper.class})
    public static class SignalServiceConfiguration {
    }

    @Configuration
    @ConditionalOnMissingBean(MonitoringService.class)
    @Import({MonitoringProviderService.class})
    public static class MonitoringServiceConfiguration {
    }

    @ConditionalOnWebApplication
    @Import({MonitoringController.class, SignalController.class})
    @Configuration
    public static class MonitoringWebConfiguration {
    }

}
