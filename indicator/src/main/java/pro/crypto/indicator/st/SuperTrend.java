package pro.crypto.indicator.st;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.atr.ATRRequest;
import pro.crypto.indicator.atr.AverageTrueRange;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.SUPER_TREND;

public class SuperTrend implements Indicator<STResult> {

    private final Tick[] originalData;
    private final int period;
    private final double multiplier;

    private STResult[] result;

    private BigDecimal[] upperBandValues;
    private BigDecimal[] lowerBandValues;
    private BigDecimal[] superTrendValues;

    public SuperTrend(IndicatorRequest creationRequest) {
        STRequest request = (STRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.multiplier = request.getMultiplier();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return SUPER_TREND;
    }

    @Override
    public void calculate() {
        BigDecimal[] atrValues = calculateAverageTrueRangeValues();
        calculateUpperBands(atrValues);
        calculateLowerBands(atrValues);
        calculateSuperTrendValues();
        buildSupertrendResult();
    }

    @Override
    public STResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
        checkShift(multiplier);
    }

    private BigDecimal[] calculateAverageTrueRangeValues() {
        return IndicatorResultExtractor.extract(calculateAverageTrueRange());
    }

    private SimpleIndicatorResult[] calculateAverageTrueRange() {
        return new AverageTrueRange(buildATRRequest()).getResult();
    }

    private IndicatorRequest buildATRRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(period)
                .build();
    }

    private void calculateUpperBands(BigDecimal[] atrValues) {
        BigDecimal[] basicUpperBandValues = calculateBasicBandValues(atrValues, BigDecimal::add);
        calculateFinalUpperBandValues(basicUpperBandValues);
    }

    private void calculateFinalUpperBandValues(BigDecimal[] basicUpperBandValues) {
        upperBandValues = new BigDecimal[originalData.length];
        AtomicBoolean firstValue = new AtomicBoolean(true);
        IntStream.range(0, originalData.length)
                .forEach(idx ->
                        upperBandValues[idx] = calculateFinalBand(basicUpperBandValues[idx], firstValue, this::defineFinalUpperBand, idx));
    }

    private BigDecimal defineFinalUpperBand(BigDecimal basicUpperBandValue, int currentIndex) {
        return basicUpperBandValue.compareTo(upperBandValues[currentIndex - 1]) < 0
                || originalData[currentIndex - 1].getClose().compareTo(upperBandValues[currentIndex - 1]) > 0
                ? basicUpperBandValue
                : upperBandValues[currentIndex - 1];
    }

    private void calculateLowerBands(BigDecimal[] atrValues) {
        BigDecimal[] basicLowerBandValues = calculateBasicBandValues(atrValues, BigDecimal::subtract);
        calculateFinalLowerBandValues(basicLowerBandValues);
    }

    private void calculateFinalLowerBandValues(BigDecimal[] basicLowerBandValues) {
        lowerBandValues = new BigDecimal[originalData.length];
        AtomicBoolean firstValue = new AtomicBoolean(true);
        IntStream.range(0, originalData.length)
                .forEach(idx ->
                        lowerBandValues[idx] = calculateFinalBand(basicLowerBandValues[idx], firstValue, this::defineFinalLowerBand, idx));
    }

    private BigDecimal[] calculateBasicBandValues(BigDecimal[] atrValues, BiFunction<BigDecimal, BigDecimal, BigDecimal> bandFunction) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateBasicBand(atrValues[idx], bandFunction, idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateBasicBand(BigDecimal atrValue, BiFunction<BigDecimal, BigDecimal, BigDecimal> bandFunction, int currentIndex) {
        return nonNull(atrValue)
                ? calculateBasicBandValue(atrValue, bandFunction, currentIndex)
                : null;
    }

    // UPPER = (HIGH + LOW) / 2 + MULTIPLIER * ATR
    // LOWER = (HIGH + LOW) / 2 - MULTIPLIER * ATR
    private BigDecimal calculateBasicBandValue(BigDecimal atrValue, BiFunction<BigDecimal, BigDecimal, BigDecimal> bandFunction, int currentIndex) {
        BigDecimal averageRange = calculateAverageRange(originalData[currentIndex]);
        return bandFunction.apply(averageRange, new BigDecimal(multiplier).multiply(atrValue));
    }

    private BigDecimal calculateAverageRange(Tick originalDatum) {
        return MathHelper.divide(originalDatum.getHigh().add(originalDatum.getLow()), new BigDecimal(2));
    }

    private BigDecimal defineFinalLowerBand(BigDecimal basicLowerBandValue, int currentIndex) {
        return basicLowerBandValue.compareTo(lowerBandValues[currentIndex - 1]) > 0
                || originalData[currentIndex - 1].getClose().compareTo(lowerBandValues[currentIndex - 1]) < 0
                ? basicLowerBandValue
                : lowerBandValues[currentIndex - 1];
    }

    private BigDecimal calculateFinalBand(BigDecimal basicBandValue, AtomicBoolean firstValue,
                                          BiFunction<BigDecimal, Integer, BigDecimal> finalBandFunction, int currentIndex) {
        return nonNull(basicBandValue)
                ? calculateFinalBandValue(basicBandValue, firstValue, finalBandFunction, currentIndex)
                : null;
    }

    private BigDecimal calculateFinalBandValue(BigDecimal basicBandValue, AtomicBoolean firstValue,
                                               BiFunction<BigDecimal, Integer, BigDecimal> finalBandFunction, int currentIndex) {
        if (firstValue.get()) {
            firstValue.set(false);
            return basicBandValue;
        }
        return finalBandFunction.apply(basicBandValue, currentIndex);
    }

    private void calculateSuperTrendValues() {
        superTrendValues = new BigDecimal[originalData.length];
        AtomicBoolean firstValues = new AtomicBoolean(true);
        IntStream.range(0, originalData.length)
                .forEach(idx -> superTrendValues[idx] = calculateSuperTrend(idx, firstValues));
    }

    private BigDecimal calculateSuperTrend(int currentIndex, AtomicBoolean firstValues) {
        return nonNull(upperBandValues[currentIndex]) && nonNull(lowerBandValues[currentIndex])
                ? calculateSuperTrendValue(currentIndex, firstValues)
                : null;
    }

    private BigDecimal calculateSuperTrendValue(int currentIndex, AtomicBoolean firstValues) {
        if (firstValues.get()) {
            firstValues.set(false);
            return upperBandValues[currentIndex];
        }

        return defineSuperTrend(currentIndex);
    }

    private BigDecimal defineSuperTrend(int currentIndex) {
        if (isPreviousSuperTrendUpper(currentIndex)) {
            return defineUpperSuperTrend(currentIndex);
        } else {
            return defineLowerSuperTrend(currentIndex);
        }
    }

    private boolean isPreviousSuperTrendUpper(int currentIndex) {
        return superTrendValues[currentIndex - 1].compareTo(upperBandValues[currentIndex - 1]) == 0;
    }

    private BigDecimal defineUpperSuperTrend(int currentIndex) {
        if (originalData[currentIndex].getClose().compareTo(upperBandValues[currentIndex]) < 0) {
            return upperBandValues[currentIndex];
        }
        return lowerBandValues[currentIndex];
    }

    private BigDecimal defineLowerSuperTrend(int currentIndex) {
        if (originalData[currentIndex].getClose().compareTo(lowerBandValues[currentIndex]) > 0) {
            return lowerBandValues[currentIndex];
        }
        return upperBandValues[currentIndex];
    }

    private void buildSupertrendResult() {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new STResult(originalData[idx].getTickTime(), superTrendValues[idx]))
                .toArray(STResult[]::new);
    }

}
