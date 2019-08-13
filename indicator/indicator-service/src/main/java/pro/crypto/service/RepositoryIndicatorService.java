package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.crypto.factory.IndicatorFactory;
import pro.crypto.factory.IndicatorRequestFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorCalculationRequest;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.request.TickTimeFindRequest;
import pro.crypto.response.IndicatorResult;

import static java.time.LocalTime.MAX;
import static java.time.LocalTime.MIN;
import static pro.crypto.model.tick.TimeFrame.ONE_DAY;

@Service
@Slf4j
@AllArgsConstructor
public class RepositoryIndicatorService implements IndicatorService {

    private final IndicatorRequestFactory requestFactory;
    private final IndicatorFactory indicatorFactory;
    private final TickService tickService;

    public IndicatorResult[] calculate(IndicatorCalculationRequest request) {
        log.trace("Calculating indicator {request: {}}", request);
        Tick[] ticks = tickService.getTicksByTime(buildTickTimeFindRequest(request)).getTicks();
        IndicatorRequest indicatorRequest = requestFactory.buildRequest(ticks, request.getIndicatorType(), request.getConfiguration());
        Indicator indicator = createIndicator(request, indicatorRequest);
        IndicatorResult[] result = indicator.getResult();
        log.info("Calculated indicator {request: {}}", request);
        return result;
    }

    private TickTimeFindRequest buildTickTimeFindRequest(IndicatorCalculationRequest indicatorCalculationRequest) {
        return TickTimeFindRequest.builder()
                .marketId(indicatorCalculationRequest.getMarketId())
                .timeFrame(indicatorCalculationRequest.getTimeFrame())
                .from(indicatorCalculationRequest.getFrom())
                .to(indicatorCalculationRequest.getTo())
                .build();
    }

    private Indicator createIndicator(IndicatorCalculationRequest request, IndicatorRequest indicatorRequest) {
        if (request.getIndicatorType().isPivotPoint()) {
            Tick[] oneDayData = getOneDayTickData(indicatorRequest.getOriginalData(), request);
            return indicatorFactory.create(indicatorRequest, request.getIndicatorType(), oneDayData);
        }
        return indicatorFactory.create(indicatorRequest, request.getIndicatorType(), null);
    }

    private Tick[] getOneDayTickData(Tick[] originalData, IndicatorCalculationRequest request) {
        return request.getTimeFrame() == ONE_DAY
                ? originalData
                : tickService.getTicksByTime(buildPivotPointTickTimeFindRequest(request)).getTicks();
    }

    private TickTimeFindRequest buildPivotPointTickTimeFindRequest(IndicatorCalculationRequest indicatorCalculationRequest) {
        return TickTimeFindRequest.builder()
                .marketId(indicatorCalculationRequest.getMarketId())
                .timeFrame(ONE_DAY)
                .from(indicatorCalculationRequest.getFrom().toLocalDate().atTime(MIN))
                .to(indicatorCalculationRequest.getTo().toLocalDate().atTime(MAX))
                .build();
    }

}
