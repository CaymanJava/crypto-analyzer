package pro.crypto.factory;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pro.crypto.exception.UnknownTypeException;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.StrategyRequest;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Component
@AllArgsConstructor
public class StrategyRequestFactory {

    private final StrategyRequestTypeResolver typeResolver;

    public StrategyRequest buildRequest(Tick[] ticks, StrategyType strategyType, String configuration) {
        Class<? extends StrategyRequest> strategyRequestClass = getStrategyRequestClass(strategyType);
        StrategyRequest strategyRequest = new Gson().fromJson(configuration, strategyRequestClass);
        strategyRequest.setOriginalData(ticks);
        return strategyRequest;
    }

    private Class<? extends StrategyRequest> getStrategyRequestClass(StrategyType strategyType) {
        return ofNullable(typeResolver.resolve(strategyType))
                .orElseThrow(() -> new UnknownTypeException(format("Unknown strategy type {strategyType: {%s}}", strategyType)));
    }

}
