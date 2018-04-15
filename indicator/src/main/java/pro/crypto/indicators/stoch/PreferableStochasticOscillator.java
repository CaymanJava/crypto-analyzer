package pro.crypto.indicators.stoch;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.request.StochRequest;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.result.StochResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.MODIFIED_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.PREFERABLE_STOCHASTIC_OSCILLATOR;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class PreferableStochasticOscillator implements Indicator<StochResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int fastPeriod;
    private final int slowPeriod;

    private StochResult[] result;

    public PreferableStochasticOscillator(StochRequest request) {
        this.originalData = request.getOriginalData();
        this.movingAverageType = isNull(request.getMovingAverageType()) ? MODIFIED_MOVING_AVERAGE : request.getMovingAverageType();
        this.fastPeriod = request.getFastPeriod();
        this.slowPeriod = request.getSlowPeriod();
        checkIncomingData();
    }


    @Override
    public IndicatorType getType() {
        return PREFERABLE_STOCHASTIC_OSCILLATOR;
    }

    @Override
    public void calculate() {
        result = new StochResult[originalData.length];
        BigDecimal[] fastStochastic = calculateFastStochasticOscillator();
        BigDecimal[] slowStochastic = calculateSlowStochasticOscillator(fastStochastic);
        buildPreferableStochasticOscillatorResult(fastStochastic, slowStochastic);
    }

    @Override
    public StochResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkPeriod(fastPeriod);
        checkPeriod(slowPeriod);
        checkOriginalDataSize(originalData, fastPeriod + slowPeriod + slowPeriod);
        checkMovingAverageType(movingAverageType);
    }

    private BigDecimal[] calculateFastStochasticOscillator() {
        return Stream.of(calculateStochasticOscillator())
                .map(StochResult::getSlowStochastic)
                .toArray(BigDecimal[]::new);
    }

    private StochResult[] calculateStochasticOscillator() {
        return new StochasticOscillator(buildStochasticRequest()).getResult();
    }

    private StochRequest buildStochasticRequest() {
        return StochRequest.builder()
                .originalData(originalData)
                .movingAverageType(movingAverageType)
                .fastPeriod(fastPeriod)
                .slowPeriod(slowPeriod)
                .build();
    }

    private BigDecimal[] calculateSlowStochasticOscillator(BigDecimal[] fastStochastic) {
        BigDecimal[] slowStochastic = Stream.of(calculateMovingAverageResult(fastStochastic))
                .map(MAResult::getIndicatorValue)
                .toArray(BigDecimal[]::new);
        BigDecimal[] result = new BigDecimal[fastStochastic.length];
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = nonNull(fastStochastic[currentIndex]) ? slowStochastic[currentIndex - fastPeriod - 1] : null;
        }
        return result;
    }

    private MAResult[] calculateMovingAverageResult(BigDecimal[] fastStochastic) {
        return MovingAverageFactory.create(buildModifiedMovingAverageRequest(fastStochastic)).getResult();
    }

    private MARequest buildModifiedMovingAverageRequest(BigDecimal[] fastStochastic) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(fastStochastic))
                .period(slowPeriod)
                .priceType(CLOSE)
                .indicatorType(MODIFIED_MOVING_AVERAGE)
                .build();
    }

    private void buildPreferableStochasticOscillatorResult(BigDecimal[] fastStochastic, BigDecimal[] slowStochastic) {
        for (int i = 0; i < result.length; i++) {
            result[i] = new StochResult(originalData[i].getTickTime(), fastStochastic[i], slowStochastic[i]);
        }
    }

}
