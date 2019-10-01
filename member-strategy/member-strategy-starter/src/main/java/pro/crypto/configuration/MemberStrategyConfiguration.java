package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.MemberStrategyProperties;
import pro.crypto.service.ExecutionTimeCounter;
import pro.crypto.service.ExecutionTimeService;
import pro.crypto.service.MemberStrategyControlService;
import pro.crypto.service.MemberStrategyMapper;
import pro.crypto.service.MemberStrategyService;
import pro.crypto.service.MemberStrategySupervisor;
import pro.crypto.service.RepositoryMemberStrategyService;
import pro.crypto.web.MemberStrategyControlController;
import pro.crypto.web.MemberStrategyController;

@Configuration
@EnableConfigurationProperties({MemberStrategyProperties.class})
public class MemberStrategyConfiguration {

    @Configuration
    @ConditionalOnMissingBean(MemberStrategyService.class)
    @Import({RepositoryMemberStrategyService.class, MemberStrategyMapper.class})
    public static class MemberStrategyServiceConfiguration {
    }

    @Configuration
    @ConditionalOnMissingBean(MemberStrategyControlService.class)
    @Import({MemberStrategySupervisor.class})
    public static class MemberStrategyControlServiceConfiguration {
    }

    @Configuration
    @ConditionalOnMissingBean(ExecutionTimeService.class)
    @Import({ExecutionTimeCounter.class})
    public static class ExecutionTimeServiceConfiguration {
    }

    @ConditionalOnWebApplication
    @Import({MemberStrategyController.class, MemberStrategyControlController.class})
    @Configuration
    public static class MarketStrategyWebConfiguration {
    }

}
