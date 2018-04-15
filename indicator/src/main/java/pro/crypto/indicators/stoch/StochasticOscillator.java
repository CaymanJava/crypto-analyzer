package pro.crypto.indicators.stoch;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.request.StochRequest;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.result.StochResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.MODIFIED_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.STOCHASTIC_OSCILLATOR;
import static pro.crypto.model.tick.PriceType.CLOSE;

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
        BigDecimal[] minValues = calculateMinimumValues();
        BigDecimal[] maxValues = calculateMaximumValues();
        return calculateFastStochasticOscillator(minValues, maxValues);
    }

    private BigDecimal[] calculateMinimumValues() {
        BigDecimal[] minValues = new BigDecimal[originalData.length];
        for (int currentIndex = fastPeriod - 1; currentIndex < minValues.length; currentIndex++) {
            minValues[currentIndex] = MathHelper.min(extractLowValuesForComparing(currentIndex));
        }
        return minValues;
    }

    private BigDecimal[] extractLowValuesForComparing(int currentIndex) {
        return Arrays.copyOfRange(extractLowValues(), currentIndex - fastPeriod + 1, currentIndex + 1);
    }

    private BigDecimal[] extractLowValues() {
        return Stream.of(originalData)
                .map(Tick::getLow)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] calculateMaximumValues() {
        BigDecimal[] maxValues = new BigDecimal[originalData.length];
        for (int currentIndex = fastPeriod - 1; currentIndex < maxValues.length; currentIndex++) {
            maxValues[currentIndex] = MathHelper.max(extractHighValuesForComparing(currentIndex));
        }
        return maxValues;
    }

    private BigDecimal[] extractHighValuesForComparing(int currentIndex) {
        return Arrays.copyOfRange(extractHighValues(), currentIndex - fastPeriod + 1, currentIndex + 1);
    }

    private BigDecimal[] extractHighValues() {
        return Stream.of(originalData)
                .map(Tick::getHigh)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] calculateFastStochasticOscillator(BigDecimal[] minValues, BigDecimal[] maxValues) {
        BigDecimal[] fastStochastic = new BigDecimal[originalData.length];
        for (int currentIndex = 0; currentIndex < fastStochastic.length; currentIndex++) {
            fastStochastic[currentIndex] = calculateFastStochasticOscillator(minValues[currentIndex], maxValues[currentIndex], currentIndex);
        }
        return fastStochastic;
    }

    private BigDecimal calculateFastStochasticOscillator(BigDecimal minValue, BigDecimal maxValue, int currentIndex) {
        return nonNull(minValue) && nonNull(maxValue) && nonNull(originalData[currentIndex].getClose())
                ? calculateFastStochasticOscillatorValue(minValue, maxValue, originalData[currentIndex].getClose())
                : null;
    }

    // %K = 100 * (CLOSE - MINn)/(MAXn - MINn)
    private BigDecimal calculateFastStochasticOscillatorValue(BigDecimal minValue, BigDecimal maxValue, BigDecimal close) {
        BigDecimal stochCoefficient = MathHelper.divide(close.subtract(minValue), maxValue.subtract(minValue));
        return nonNull(stochCoefficient)
                ? new BigDecimal(100).multiply(stochCoefficient)
                : null;
    }

    private BigDecimal[] calculateSlowStochasticOscillator(BigDecimal[] fastStochastic) {
        BigDecimal[] slowStochastic = Stream.of(calculateMovingAverageResult(fastStochastic))
                .map(MAResult::getIndicatorValue)
                .toArray(BigDecimal[]::new);
        BigDecimal[] result = new BigDecimal[fastStochastic.length];
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = nonNull(fastStochastic[currentIndex]) ? slowStochastic[currentIndex - fastPeriod + 1] : null;
        }
        return result;
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
        for (int i = 0; i < result.length; i++) {
            result[i] = new StochResult(originalData[i].getTickTime(), fastStochastic[i], slowStochastic[i]);
        }
    }

}
