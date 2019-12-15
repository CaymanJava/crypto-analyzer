package pro.crypto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "social.service")
public class SocialProperties {

    private Map<Provider, SocialProviderProperties> providers;

    public SocialProviderProperties getConfiguration(Provider provider) {
        return providers.get(provider);
    }

}
