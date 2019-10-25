package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.configuration.infrastructure.market.MarketExpressionProperties;
import pro.crypto.configuration.infrastructure.monitoring.MonitoringExpressionProperties;
import pro.crypto.configuration.quartz.JobConfiguration;
import pro.crypto.configuration.quartz.QuartzProperties;
import pro.crypto.scheduler.MarketSynchronisationJob;
import pro.crypto.scheduler.MonitoringJob;

@Configuration
@AutoConfigureBefore(JpaRepositoriesAutoConfiguration.class)
@EnableConfigurationProperties({QuartzProperties.class, MarketExpressionProperties.class, MonitoringExpressionProperties.class})
public class SchedulerAutoConfiguration {

    @Import({JobConfiguration.class})
    @Configuration
    public static class SchedulerInitializationConfiguration {
    }

    @Import({MarketSynchronisationJob.class, MonitoringJob.class})
    @Configuration
    public static class JobsConfiguration {
    }

}
