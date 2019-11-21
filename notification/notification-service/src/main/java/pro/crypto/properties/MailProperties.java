package pro.crypto.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "notification.email")
public class MailProperties {

    private boolean queueEnabled;

    private String queueName;

    private String exchange;

    private String routingKey;

    private Integer inboundConcurrentConsumers;

}
