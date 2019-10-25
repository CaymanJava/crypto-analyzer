package pro.crypto.configuration.infrastructure.market;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "scheduler.market")
public class MarketExpressionProperties {

    private String marketSynchronisationExpression;

}
