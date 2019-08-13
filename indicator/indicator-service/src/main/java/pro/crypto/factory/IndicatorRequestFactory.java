package pro.crypto.factory;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pro.crypto.exception.UnknownTypeException;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Component
@AllArgsConstructor
public class IndicatorRequestFactory {

    private final JsonParser parser;
    private final IndicatorRequestTypeResolver typeResolver;

    public IndicatorRequest buildRequest(Tick[] ticks, IndicatorType indicatorType, String configuration) {
        Class<? extends IndicatorRequest> indicatorRequestClass = getIndicatorRequestClass(indicatorType);
        IndicatorRequest indicatorRequest = parser.parse(configuration, indicatorRequestClass);
        indicatorRequest.setOriginalData(ticks);
        return indicatorRequest;
    }

    private Class<? extends IndicatorRequest> getIndicatorRequestClass(IndicatorType indicatorType) {
        return ofNullable(typeResolver.resolve(indicatorType))
                .orElseThrow(() -> new UnknownTypeException(format("Unknown indicator type {indicatorType: {%s}}", indicatorType)));
    }

}
