package pro.crypto.indicators.stoch;

import pro.crypto.helper.*;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.request.StochRequest;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.result.StochResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.MODIFIED_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.STOCHASTIC_OSCILLATOR;
import static pro.crypto.model.tick.PriceType.CLOSE;
import static pro.crypto.model.tick.PriceType.HIGH;
import static pro.crypto.model.tick.PriceType.LOW;

public class StochasticOscillator implements Indicator<StochResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int fastPeriod;
    private final int slowPeriod;

    private StochResult[] result;

    public StochasticOscillator(StochRequest request) {
        this.originalData = request.getOriginalData();
        this.movingAverageType = isNull(request.getMovingAverageType()) ? MODIFIED_MOVING_AVERAGE : request.getMovingAverageType();
        this.fastPeriod = request.getFastPeriod();
        this.slowPeriod = request.getSlowPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return STOCHASTIC_OSCILLATOR;
    }

    @Override
    public void calculate() {
        result = new StochResult[originalData.length];
        BigDecimal[] fastStochastic = calculateFastStochasticOscillator();
        BigDecimal[] slowStochastic = calculateSlowStochasticOscillator(fastStochastic);
        buildStochasticOscillatorResult(fastStochastic, slowStochastic);
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
        checkOriginalDataSize(originalData, fastPeriod + slowPeriod);
        checkPeriod(fastPeriod);
        checkPeriod(slowPeriod);
        checkMovingAverageType(movingAverageType);
    }

    private BigDecimal[] calculateFastStochasticOscillator() {
        BigDecimal[] minValues = MinMaxFinder.findMinValues(PriceExtractor.extractValuesByType(originalData, LOW), fastPeriod);
        BigDecimal[] maxValues = MinMaxFinder.findMaxValues(PriceExtractor.extractValuesByType(originalData, HIGH), fastPeriod);
        return calculateFastStochasticOscillator(minValues, maxValues);
    }

    private BigDecimal[] calculateFastStochasticOscillator(BigDecimal[] minValues, BigDecimal[] maxValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateFastStochasticOscillator(minValues[idx], maxValues[idx], idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateFastStochasticOscillator(BigDecimal minValue, BigDecimal maxValue, int currentIndex) {
        return nonNull(minValue) && nonNull(maxValue) && nonNull(originalData[currentIndex].getClose())
                ? calculateFastStochasticOscillatorValue(minValue, maxValue, originalData[currentIndex].getClose())
                : null;
    }

    // %K = 100 * (CLOSE - MINn)/(MAXn - MINn)
    private BigDecimal calculateFastStochasticOscillatorValue(BigDecimal minValue, BigDecimal maxValue, BigDecimal close) {
        return MathHelper.divide(close.subtract(minValue).multiply(new BigDecimal(100)), maxValue.subtract(minValue));
    }

    private BigDecimal[] calculateSlowStochasticOscillator(BigDecimal[] fastStochastic) {
        BigDecimal[] slowStochastic = IndicatorResultExtractor.extract(calculateMovingAverageResult(fastStochastic));
        return IntStream.range(0, result.length)
                .mapToObj(idx -> nonNull(fastStochastic[idx]) ? slowStochastic[idx - fastPeriod + 1] : null)
                .toArray(BigDecimal[]::new);
    }

    private MAResult[] calculateMovingAverageResult(BigDecimal[] fastStochastic) {
        return MovingAverageFactory.create(buildMovingAverageRequest(fastStochastic)).getResult();
    }

    private MARequest buildMovingAverageRequest(BigDecimal[] fastStochastic) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(fastStochastic))
                .period(slowPeriod)
                .priceType(CLOSE)
                .indicatorType(movingAverageType)
                .build();
    }

    private void buildStochasticOscillatorResult(BigDecimal[] fastStochastic, BigDecimal[] slowStochastic) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new StochResult(originalData[idx].getTickTime(), fastStochastic[idx], slowStochastic[idx]));
    }

}
