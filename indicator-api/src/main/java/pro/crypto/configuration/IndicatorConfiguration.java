package pro.crypto.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(value = {"pro.crypto.service", "pro.crypto.factory"})
@Configuration
public class IndicatorConfiguration {
}
