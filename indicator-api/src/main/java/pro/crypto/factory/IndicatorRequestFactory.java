package pro.crypto.factory;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

@Component
@AllArgsConstructor
public class IndicatorRequestFactory {

    private final JsonParser parser;

    public IndicatorRequest buildRequest(Tick[] ticks, IndicatorType indicatorType, String configuration) {
        IndicatorRequest indicatorRequest = parser.parse(configuration, indicatorType.getRequestClass());
        indicatorRequest.setOriginalData(ticks);
        return indicatorRequest;
    }

}
