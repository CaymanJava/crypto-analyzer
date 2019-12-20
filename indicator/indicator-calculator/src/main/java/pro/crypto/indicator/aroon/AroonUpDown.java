package pro.crypto.indicator.aroon;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.Arrays.copyOfRange;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.helper.PriceVolumeExtractor.extractPrices;
import static pro.crypto.model.indicator.IndicatorType.AROON_UP_DOWN;
import static pro.crypto.model.tick.PriceType.HIGH;
import static pro.crypto.model.tick.PriceType.LOW;

public class AroonUpDown implements Indicator<AroonResult> {

    private final Tick[] originalData;
    private final int period;

    private AroonResult[] result;

    public AroonUpDown(IndicatorRequest creationRequest) {
        AroonRequest request = (AroonRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return AROON_UP_DOWN;
    }

    @Override
    public void calculate() {
        result = new AroonResult[originalData.length];
        BigDecimal[] aroonUpValues = calculateAroonUpValues();
        BigDecimal[] aroonDownValues = calculateAroonDownValues();
        calculateAroonOscillatorValues(aroonUpValues, aroonDownValues);
    }

    @Override
    public AroonResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
    }

    private BigDecimal[] calculateAroonUpValues() {
        Integer[] daysAfterMaxValues = calculateDaysAfterMaxValues();
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateAroon(daysAfterMaxValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private Integer[] calculateDaysAfterMaxValues() {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateDaysAfter(HIGH, idx, this::findMaxValueIndex))
                .toArray(Integer[]::new);
    }

    private Integer findMaxValueIndex(BigDecimal[] highValues) {
        return findIndex(highValues, (a, b) -> a.compareTo(b) >= 0);
    }

    private BigDecimal[] calculateAroonDownValues() {
        Integer[] daysAfterMinValues = calculateDaysAfterMinValues();
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateAroon(daysAfterMinValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private Integer[] calculateDaysAfterMinValues() {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateDaysAfter(LOW, idx, this::findMinValueIndex))
                .toArray(Integer[]::new);
    }

    private Integer findMinValueIndex(BigDecimal[] lowValues) {
        return findIndex(lowValues, (a, b) -> a.compareTo(b) <= 0);
    }

    private Integer calculateDaysAfter(PriceType priceType, int currentIndex, Function<BigDecimal[], Integer> findIndex) {
        return currentIndex >= period
                ? calculateDaysAfterValue(priceType, currentIndex, findIndex)
                : null;
    }

    private Integer calculateDaysAfterValue(PriceType priceType, int currentIndex, Function<BigDecimal[], Integer> findIndex) {
        BigDecimal[] shortCutValues = copyOfRange(extractPrices(originalData, priceType), currentIndex - period, currentIndex + 1);
        return period - findIndex.apply(shortCutValues);
    }

    private Integer findIndex(BigDecimal[] values, BiFunction<BigDecimal, BigDecimal, Boolean> compareFunction) {
        return IntStream.range(0, values.length)
                .reduce((a, b) -> compareFunction.apply(values[a], values[b]) ? a : b)
                .orElse(0);
    }

    private BigDecimal calculateAroon(Integer days) {
        return nonNull(days)
                ? calculateAroonValue(days)
                : null;
    }

    private BigDecimal calculateAroonValue(Integer days) {
        return MathHelper.divide(new BigDecimal(period).subtract(new BigDecimal(days)).multiply(new BigDecimal(100)),
                new BigDecimal(period));
    }

    private void calculateAroonOscillatorValues(BigDecimal[] aroonUpValues, BigDecimal[] aroonDownValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = buildAroonResult(aroonUpValues[idx], aroonDownValues[idx], idx));
    }

    private AroonResult buildAroonResult(BigDecimal aroonUpValue, BigDecimal aroonDownValue, int idx) {
        return new AroonResult(
                originalData[idx].getTickTime(),
                aroonUpValue,
                aroonDownValue,
                calculateAroonOscillator(aroonUpValue, aroonDownValue)
        );
    }

    private BigDecimal calculateAroonOscillator(BigDecimal aroonUpValue, BigDecimal aroonDownValue) {
        return nonNull(aroonUpValue) && nonNull(aroonDownValue)
                ? aroonUpValue.subtract(aroonDownValue)
                : null;
    }

}
