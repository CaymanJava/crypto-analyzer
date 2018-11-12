package pro.crypto.indicator.stoch;

import pro.crypto.helper.*;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.IndicatorType.MODIFIED_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.STOCHASTIC_OSCILLATOR;
import static pro.crypto.model.tick.PriceType.*;

public class StochasticOscillator implements Indicator<StochResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int fastStochPeriod;
    private final int slowStochPeriod;

    private StochResult[] result;

    public StochasticOscillator(IndicatorRequest creationRequest) {
        StochRequest request = (StochRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.movingAverageType = extractMovingAverageType(request);
        this.fastStochPeriod = request.getFastStochPeriod();
        this.slowStochPeriod = request.getSlowStochPeriod();
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

    private IndicatorType extractMovingAverageType(StochRequest request) {
        return ofNullable(request.getMovingAverageType())
                .orElse(MODIFIED_MOVING_AVERAGE);
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, fastStochPeriod + slowStochPeriod);
        checkPeriod(fastStochPeriod);
        checkPeriod(slowStochPeriod);
        checkMovingAverageType(movingAverageType);
    }

    private BigDecimal[] calculateFastStochasticOscillator() {
        BigDecimal[] minValues = MinMaxFinder.findMinValues(PriceVolumeExtractor.extractPrices(originalData, LOW), fastStochPeriod);
        BigDecimal[] maxValues = MinMaxFinder.findMaxValues(PriceVolumeExtractor.extractPrices(originalData, HIGH), fastStochPeriod);
        return calculateFastStochasticOscillator(minValues, maxValues);
    }

    private BigDecimal[] calculateFastStochasticOscillator(BigDecimal[] minValues, BigDecimal[] maxValues) {
        BigDecimal[] fastStochastic = new BigDecimal[originalData.length];
        IntStream.range(1, originalData.length)
                .forEach(idx -> fastStochastic[idx] = calculateFastStochasticOscillator(minValues[idx], maxValues[idx], fastStochastic[idx - 1], idx));
        return fastStochastic;
    }

    private BigDecimal calculateFastStochasticOscillator(BigDecimal minValue, BigDecimal maxValue, BigDecimal previousStochastic, int currentIndex) {
        return nonNull(minValue) && nonNull(maxValue) && nonNull(originalData[currentIndex].getClose())
                ? calculateFastStochasticOscillatorValue(minValue, maxValue, previousStochastic, originalData[currentIndex].getClose())
                : null;
    }

    // %K = 100 * (CLOSE - MINn)/(MAXn - MINn)
    private BigDecimal calculateFastStochasticOscillatorValue(BigDecimal minValue, BigDecimal maxValue, BigDecimal previousStochastic, BigDecimal close) {
        if (maxValue.subtract(minValue).compareTo(BigDecimal.ZERO) > 0) {
            return MathHelper.divide(close.subtract(minValue).multiply(new BigDecimal(100)), maxValue.subtract(minValue));
        }
        return previousStochastic;
    }

    private BigDecimal[] calculateSlowStochasticOscillator(BigDecimal[] fastStochastic) {
        BigDecimal[] slowStochastic = IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverageResult(fastStochastic));
        return IntStream.range(0, result.length)
                .mapToObj(idx -> nonNull(fastStochastic[idx]) ? slowStochastic[idx - fastStochPeriod + 1] : null)
                .toArray(BigDecimal[]::new);
    }

    private SimpleIndicatorResult[] calculateMovingAverageResult(BigDecimal[] fastStochastic) {
        return MovingAverageFactory.create(buildMovingAverageRequest(fastStochastic)).getResult();
    }

    private IndicatorRequest buildMovingAverageRequest(BigDecimal[] fastStochastic) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(fastStochastic))
                .period(slowStochPeriod)
                .priceType(CLOSE)
                .indicatorType(movingAverageType)
                .build();
    }

    private void buildStochasticOscillatorResult(BigDecimal[] fastStochastic, BigDecimal[] slowStochastic) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new StochResult(originalData[idx].getTickTime(), fastStochastic[idx], slowStochastic[idx]));
    }

}
