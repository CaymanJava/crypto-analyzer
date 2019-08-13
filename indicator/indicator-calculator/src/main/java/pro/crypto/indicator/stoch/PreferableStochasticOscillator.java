package pro.crypto.indicator.stoch;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.indicator.IndicatorType.MODIFIED_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.PREFERABLE_STOCHASTIC_OSCILLATOR;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class PreferableStochasticOscillator implements Indicator<StochResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int fastStochPeriod;
    private final int slowStochPeriod;

    private StochResult[] result;

    public PreferableStochasticOscillator(IndicatorRequest creationRequest) {
        StochRequest request = (StochRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.movingAverageType = isNull(request.getMovingAverageType()) ? MODIFIED_MOVING_AVERAGE : request.getMovingAverageType();
        this.fastStochPeriod = request.getFastStochPeriod();
        this.slowStochPeriod = request.getSlowStochPeriod();
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
        checkPeriod(fastStochPeriod);
        checkPeriod(slowStochPeriod);
        checkOriginalDataSize(originalData, fastStochPeriod + slowStochPeriod + slowStochPeriod);
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

    private IndicatorRequest buildStochasticRequest() {
        return StochRequest.builder()
                .originalData(originalData)
                .movingAverageType(movingAverageType)
                .fastStochPeriod(fastStochPeriod)
                .slowStochPeriod(slowStochPeriod)
                .build();
    }

    private BigDecimal[] calculateSlowStochasticOscillator(BigDecimal[] fastStochastic) {
        BigDecimal[] slowStochastic = IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverageResult(fastStochastic));
        return IntStream.range(0, fastStochastic.length)
                .mapToObj(idx -> nonNull(fastStochastic[idx]) ? slowStochastic[idx - fastStochPeriod - 1] : null)
                .toArray(BigDecimal[]::new);
    }

    private SimpleIndicatorResult[] calculateMovingAverageResult(BigDecimal[] fastStochastic) {
        return MovingAverageFactory.create(buildModifiedMovingAverageRequest(fastStochastic)).getResult();
    }

    private IndicatorRequest buildModifiedMovingAverageRequest(BigDecimal[] fastStochastic) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(fastStochastic))
                .period(slowStochPeriod)
                .priceType(CLOSE)
                .indicatorType(MODIFIED_MOVING_AVERAGE)
                .build();
    }

    private void buildPreferableStochasticOscillatorResult(BigDecimal[] fastStochastic, BigDecimal[] slowStochastic) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new StochResult(originalData[idx].getTickTime(), fastStochastic[idx], slowStochastic[idx]));
    }

}
