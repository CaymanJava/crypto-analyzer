package pro.crypto.configuration.infrastructure.monitoring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "scheduler.monitoring")
public class MonitoringExpressionProperties {

    private String monitoringExpression;

}
