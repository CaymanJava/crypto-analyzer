package pro.crypto.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "notification.email.retry")
public class MessageRetryConfig {

    private boolean enabled;

    private int maxAttempts;

    private long initialInterval;

    private double multiplier;

    private long maxInterval;

}
