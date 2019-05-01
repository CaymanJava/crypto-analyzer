package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.crypto.factory.IndicatorFactory;
import pro.crypto.factory.IndicatorRequestFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorCalculationRequest;
import pro.crypto.request.TickTimeFindRequest;

@Service
@Slf4j
@AllArgsConstructor
public class IndicatorService {

    private final IndicatorRequestFactory requestFactory;
    private final IndicatorFactory indicatorFactory;
    private final TickService tickService;

    public IndicatorResult[] calculate(IndicatorCalculationRequest request) {
        log.trace("Calculating indicator {request: {}}", request);
        Tick[] ticks = tickService.getTicksByTime(buildTickTimeFindRequest(request)).getTicks();
        IndicatorRequest indicatorRequest = requestFactory.buildRequest(ticks, request.getIndicatorType(), request.getConfiguration());
        Indicator indicator = indicatorFactory.create(indicatorRequest, request.getIndicatorType());
        return indicator.getResult();
    }

    private TickTimeFindRequest buildTickTimeFindRequest(IndicatorCalculationRequest indicatorCalculationRequest) {
        return TickTimeFindRequest.builder()
                .marketId(indicatorCalculationRequest.getMarketId())
                .timeFrame(indicatorCalculationRequest.getTimeFrame())
                .from(indicatorCalculationRequest.getFrom())
                .to(indicatorCalculationRequest.getTo())
                .build();
    }

}
