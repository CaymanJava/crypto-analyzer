package pro.crypto.configuration.quartz;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Data
@ConfigurationProperties(prefix = "scheduler")
public class QuartzProperties {

    private Properties properties;

    private Boolean disabled = false;

}
