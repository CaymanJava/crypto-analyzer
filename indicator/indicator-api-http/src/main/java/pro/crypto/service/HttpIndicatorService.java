package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pro.crypto.proxy.HttpIndicatorProxy;
import pro.crypto.request.IndicatorCalculationRequest;

@Service
@AllArgsConstructor
public class HttpIndicatorService implements IndicatorService {

    private final HttpIndicatorProxy indicatorProxy;

    @Override
    public Object[] calculate(IndicatorCalculationRequest request) {
        return indicatorProxy.calculate(request.getMarketId(), request.getTimeFrame(),
                request.getFrom(), request.getTo(),
                request.getIndicatorType(), request.getConfiguration());
    }

}
