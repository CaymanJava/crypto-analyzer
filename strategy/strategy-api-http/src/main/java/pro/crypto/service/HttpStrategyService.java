package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pro.crypto.proxy.HttpStrategyProxy;
import pro.crypto.request.StrategyCalculationRequest;

@Service
@AllArgsConstructor
public class HttpStrategyService implements StrategyService {

    private final HttpStrategyProxy strategyProxy;

    @Override
    public Object[] calculate(StrategyCalculationRequest request) {
        return strategyProxy.calculate(request.getMarketId(), request.getTimeFrame(),
                request.getFrom(), request.getTo(), request.getStrategyType(), request.getConfiguration());
    }

}
