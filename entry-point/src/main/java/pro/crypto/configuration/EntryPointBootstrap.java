package pro.crypto.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class EntryPointBootstrap extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(EntryPointBootstrap.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EntryPointBootstrap.class);
    }

}
