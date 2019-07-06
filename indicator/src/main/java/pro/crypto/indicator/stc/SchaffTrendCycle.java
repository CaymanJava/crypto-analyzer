package pro.crypto.indicator.stc;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MinMaxFinder;
import pro.crypto.indicator.macd.MACDRequest;
import pro.crypto.indicator.macd.MovingAverageConvergenceDivergence;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.SCHAFF_TREND_CYCLE;

public class SchaffTrendCycle implements Indicator<STCResult> {

    private final static BigDecimal FACTOR = new BigDecimal(0.5);

    private final Tick[] originalData;
    private final PriceType priceType;
    private final int period;
    private final int shortCycle;
    private final int longCycle;
    private final IndicatorType movingAverageType;

    private STCResult[] result;

    public SchaffTrendCycle(IndicatorRequest creationRequest) {
        STCRequest request = (STCRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.priceType = request.getPriceType();
        this.period = request.getPeriod();
        this.shortCycle = request.getShortCycle();
        this.longCycle = request.getLongCycle();
        this.movingAverageType = request.getMovingAverageType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return SCHAFF_TREND_CYCLE;
    }

    @Override
    public void calculate() {
        BigDecimal[] macdValues = calculateMACDValues();
        BigDecimal[] firstStochValues = calculateKStoch(macdValues);
        BigDecimal[] secondStochValues = calculateKStoch(firstStochValues);
        buildSchaffTrendCycleResult(secondStochValues);
    }

    @Override
    public STCResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData,longCycle + (period * 2));
        checkPeriod(period);
        checkPeriod(shortCycle);
        checkPeriod(longCycle);
        checkPriceType(priceType);
        checkMovingAverageType(movingAverageType);
    }

    private BigDecimal[] calculateMACDValues() {
        return IndicatorResultExtractor.extractIndicatorValues(calculateMACD());
    }

    private SimpleIndicatorResult[] calculateMACD() {
        return new MovingAverageConvergenceDivergence(buildMACDRequest()).getResult();
    }

    private IndicatorRequest buildMACDRequest() {
        return MACDRequest.builder()
                .originalData(originalData)
                .priceType(priceType)
                .slowPeriod(longCycle)
                .fastPeriod(shortCycle)
                .movingAverageType(movingAverageType)
                .signalPeriod(shortCycle)
                .build();
    }

    private BigDecimal[] calculateKStoch(BigDecimal[] originalValues) {
        BigDecimal[] minValues = getMinValues(originalValues);
        BigDecimal[] maxMinDiffs = getMaxMinDiff(originalValues, minValues);
        BigDecimal[] kStochValues = calculateKStoch(originalValues, minValues, maxMinDiffs);
        return calculateDStochValues(kStochValues);
    }

    private BigDecimal[] getMinValues(BigDecimal[] originalValues) {
        BigDecimal[] nonNullMacdValues = extractNonNullValues(originalValues);
        BigDecimal[] minValues = MinMaxFinder.findMinValues(nonNullMacdValues, period);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(minValues, 0, result, originalValues.length - nonNullMacdValues.length, minValues.length);
        return result;
    }

    private BigDecimal[] getMaxMinDiff(BigDecimal[] macdValues, BigDecimal[] minValues) {
        BigDecimal[] nonNullMacdValues = extractNonNullValues(macdValues);
        BigDecimal[] maxValues = MinMaxFinder.findMaxValues(nonNullMacdValues, period);
        BigDecimal[] maxValuesResult = new BigDecimal[originalData.length];
        System.arraycopy(maxValues, 0, maxValuesResult, macdValues.length - nonNullMacdValues.length, maxValues.length);
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateDifference(maxValuesResult[idx], minValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] extractNonNullValues(BigDecimal[] macdValues) {
        return Stream.of(macdValues)
                .filter(Objects::nonNull)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateDifference(BigDecimal maxValue, BigDecimal minValue) {
        return nonNull(maxValue) && nonNull(minValue)
                ? maxValue.subtract(minValue)
                : null;
    }

    private BigDecimal[] calculateKStoch(BigDecimal[] originalValues, BigDecimal[] minValues, BigDecimal[] maxMinDiffs) {
        AtomicBoolean firstValue = new AtomicBoolean(true);
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateKStoch(originalValues[idx], minValues[idx], maxMinDiffs[idx], firstValue))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateKStoch(BigDecimal originalValue, BigDecimal minValue, BigDecimal maxMinDiff, AtomicBoolean firstValue) {
        return nonNull(originalValue) && nonNull(minValue) && nonNull(maxMinDiff)
                ? calculateKStochValue(originalValue, minValue, maxMinDiff, firstValue)
                : null;
    }

    private BigDecimal calculateKStochValue(BigDecimal originalValue, BigDecimal minValue, BigDecimal maxMinDiff, AtomicBoolean firstValue) {
        if (firstValue.get() || maxMinDiff.compareTo(BigDecimal.ZERO) <= 0) {
            firstValue.set(false);
            return maxMinDiff;
        }
        return calculateKStochValue(originalValue, minValue, maxMinDiff);
    }

    // (MACD - MIN) / (MAX - MIN) * 100
    private BigDecimal calculateKStochValue(BigDecimal originalValue, BigDecimal minValue, BigDecimal maxMinDiff) {
        return MathHelper.divide(new BigDecimal(100).multiply(originalValue.subtract(minValue)), maxMinDiff);
    }

    private BigDecimal[] calculateDStochValues(BigDecimal[] kStochValues) {
        BigDecimal[] dStochValues = new BigDecimal[originalData.length];
        IntStream.range(1, originalData.length)
                .forEach(idx -> dStochValues[idx] = calculateDStoch(kStochValues[idx], dStochValues[idx - 1]));
        return dStochValues;
    }

    private BigDecimal calculateDStoch(BigDecimal kStochValue, BigDecimal dStochValue) {
        return nonNull(kStochValue)
                ? calculateDStochValue(kStochValue, dStochValue)
                : null;
    }

    // %D + (Factor * (%K - %D))
    private BigDecimal calculateDStochValue(BigDecimal kStochValue, BigDecimal dStochValue) {
        return nonNull(dStochValue)
                ? dStochValue.add(FACTOR.multiply(kStochValue.subtract(dStochValue)))
                : kStochValue;
    }

    private void buildSchaffTrendCycleResult(BigDecimal[] secondStochValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new STCResult(originalData[idx].getTickTime(), MathHelper.scaleAndRound(secondStochValues[idx])))
                .toArray(STCResult[]::new);
    }

}
