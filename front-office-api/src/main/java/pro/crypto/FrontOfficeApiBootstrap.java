package pro.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FrontOfficeApiBootstrap extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(FrontOfficeApiBootstrap.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FrontOfficeApiBootstrap.class);
    }

}
