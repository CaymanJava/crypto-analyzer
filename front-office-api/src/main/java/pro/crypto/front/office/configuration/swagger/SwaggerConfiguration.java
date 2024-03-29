package pro.crypto.front.office.configuration.swagger;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.util.List;

import static java.util.Collections.singletonList;

@Configuration
@EnableSwagger2
@ConditionalOnProperty("swagger.enable")
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .directModelSubstitute(Instant.class, String.class)
                .directModelSubstitute(LocalDate.class, String.class)
                .directModelSubstitute(LocalDateTime.class, String.class)
                .directModelSubstitute(LocalTime.class, String.class)
                .directModelSubstitute(Duration.class, String.class)
                .directModelSubstitute(Period.class, String.class)
                .directModelSubstitute(OffsetDateTime.class, String.class)
                .directModelSubstitute(OffsetTime.class, String.class)
                .ignoredParameterTypes(AuthenticationPrincipal.class, ApiIgnore.class)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(getPaths())
                .build()
                .apiInfo(apiInfo());
    }

    private static List<? extends SecurityScheme> securitySchemes() {
        return singletonList(new ApiKey("Bearer", "Authorization", "header"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Front Office API")
                .build();
    }

    private Predicate<String> getPaths() {
        return Predicates.or(
                PathSelectors.ant("/tick/**"),
                PathSelectors.ant("/indicator/**"),
                PathSelectors.ant("/strategy/**"),
                PathSelectors.ant("/market/**"),
                PathSelectors.ant("/tokens/**"),
                PathSelectors.ant("/activate/**"),
                PathSelectors.ant("/me/**")
        );
    }

}
