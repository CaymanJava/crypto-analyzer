package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.crypto.factory.StrategyFactory;
import pro.crypto.factory.StrategyRequestFactory;
import pro.crypto.model.Strategy;
import pro.crypto.model.tick.Tick;
import pro.crypto.model.tick.TimeFrame;
import pro.crypto.request.StrategyCalculationRequest;
import pro.crypto.request.StrategyRequest;
import pro.crypto.request.TickTimeFindRequest;
import pro.crypto.response.StrategyResult;

import static java.time.LocalTime.MAX;
import static java.time.LocalTime.MIN;
import static pro.crypto.helper.StrategyTypeChecker.withPivotPoints;

@Service
@Slf4j
@AllArgsConstructor
public class RepositoryStrategyService implements StrategyService {

    private final StrategyRequestFactory requestFactory;
    private final StrategyFactory strategyFactory;
    private final TickService tickService;

    @Override
    public StrategyResult[] calculate(StrategyCalculationRequest request) {
        log.trace("Calculating strategy {request: {}}", request);
        Tick[] ticks = tickService.getTicksByTime(buildTickTimeFindRequest(request)).getTicks();
        StrategyRequest strategyRequest = requestFactory.buildRequest(ticks, request.getStrategyType(), request.getConfiguration());
        Strategy strategy = createStrategy(request, strategyRequest);
        StrategyResult[] result = strategy.getResult();
        log.info("Calculated strategy {request: {}}", request);
        return result;
    }

    private TickTimeFindRequest buildTickTimeFindRequest(StrategyCalculationRequest indicatorCalculationRequest) {
        return TickTimeFindRequest.builder()
                .marketId(indicatorCalculationRequest.getMarketId())
                .timeFrame(indicatorCalculationRequest.getTimeFrame())
                .from(indicatorCalculationRequest.getFrom())
                .to(indicatorCalculationRequest.getTo())
                .build();
    }

    private Strategy createStrategy(StrategyCalculationRequest request, StrategyRequest indicatorRequest) {
        if (withPivotPoints(request.getStrategyType())) {
            Tick[] oneDayData = getOneDayTickData(indicatorRequest.getOriginalData(), request);
            return strategyFactory.create(indicatorRequest, request.getStrategyType(), oneDayData);
        }
        return strategyFactory.create(indicatorRequest, request.getStrategyType(), null);
    }

    private Tick[] getOneDayTickData(Tick[] originalData, StrategyCalculationRequest request) {
        return request.getTimeFrame() == TimeFrame.ONE_DAY
                ? originalData
                : tickService.getTicksByTime(buildPivotPointTickTimeFindRequest(request)).getTicks();
    }

    private TickTimeFindRequest buildPivotPointTickTimeFindRequest(StrategyCalculationRequest request) {
        return TickTimeFindRequest.builder()
                .marketId(request.getMarketId())
                .timeFrame(TimeFrame.ONE_DAY)
                .from(request.getFrom().toLocalDate().atTime(MIN))
                .to(request.getTo().toLocalDate().atTime(MAX))
                .build();
    }

}
