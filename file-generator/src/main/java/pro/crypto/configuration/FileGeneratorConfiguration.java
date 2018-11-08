package pro.crypto.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(value = {"pro.crypto.generator.**"})
@Configuration
public class FileGeneratorConfiguration {
}
