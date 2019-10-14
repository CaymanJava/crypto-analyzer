package pro.crypto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "member.strategy.monitoring")
public class MonitoringProperties {

    private Integer strategyMonitoringActorsSize;

    private Integer strategyCalculationActorsSize;

    private Integer decisionMakerActorsSize;

    private Integer signalSenderActorsSize;

}
