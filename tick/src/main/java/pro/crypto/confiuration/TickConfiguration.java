package pro.crypto.confiuration;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@AutoConfigureBefore(JpaRepositoriesAutoConfiguration.class)
@ComponentScan(value = {"pro.crypto.service"})
@Configuration
@EnableAsync
public class TickConfiguration {
}
