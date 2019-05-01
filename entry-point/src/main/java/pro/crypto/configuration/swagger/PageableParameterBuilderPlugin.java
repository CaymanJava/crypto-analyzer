package pro.crypto.configuration.swagger;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

@Component
@Order(10)
public class PageableParameterBuilderPlugin implements ParameterBuilderPlugin {

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(ParameterContext context) {
        MethodParameter parameter = context.methodParameter();
        Class<?> type = parameter.getParameterType();
        if (nonNull(type) && Pageable.class.isAssignableFrom(type)) {
            List<Parameter> parameters = Arrays.asList(
                    context.parameterBuilder()
                            .parameterType("query").name("page").modelRef(new ModelRef("int"))
                            .description("Results page you want to retrieve (0..N)")
                            .required(false)
                            .build(),
                    new ParameterBuilder()
                            .required(false)
                            .parameterType("query").name("size").modelRef(new ModelRef("int"))
                            .description("Number of records per page")
                            .build(),
                    new ParameterBuilder()
                            .required(false)
                            .parameterType("query").name("sort").modelRef(new ModelRef("array", new ModelRef("string"))).allowMultiple(true)
                            .description("Sorting criteria in the format: property(,asc|desc). Default sort order is ascending.")
                            .build());

            context.getOperationContext().operationBuilder().parameters(parameters);
        }
    }
}
