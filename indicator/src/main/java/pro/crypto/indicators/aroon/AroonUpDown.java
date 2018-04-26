package pro.crypto.indicators.aroon;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.AroonRequest;
import pro.crypto.model.result.AroonResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.util.Arrays.copyOfRange;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.AROON_UP_DOWN;

public class AroonUpDown implements Indicator<AroonResult> {

    private final Tick[] originalData;
    private final int period;

    private AroonResult[] result;

    public AroonUpDown(AroonRequest request) {
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
        BigDecimal[] aroonUpValues = new BigDecimal[originalData.length];
        for (int currentIndex = 0; currentIndex < originalData.length; currentIndex++) {
            aroonUpValues[currentIndex] = calculateAroon(daysAfterMaxValues[currentIndex]);
        }
        return aroonUpValues;
    }

    private Integer[] calculateDaysAfterMaxValues() {
        Integer[] daysAfterMaxPrice = new Integer[originalData.length];
        BigDecimal[] highValues = extractHighValues();
        for (int currentIndex = period; currentIndex < originalData.length; currentIndex++) {
            daysAfterMaxPrice[currentIndex] = calculateDaysAfterMaxValue(copyOfRange(highValues, currentIndex - period, currentIndex + 1));
        }
        return daysAfterMaxPrice;
    }

    private BigDecimal[] extractHighValues() {
        return Stream.of(originalData)
                .map(Tick::getHigh)
                .toArray(BigDecimal[]::new);
    }

    private Integer calculateDaysAfterMaxValue(BigDecimal[] highValues) {
        Integer maxValueIndex = findMaxValueIndex(highValues);
        return period - maxValueIndex;
    }

    private Integer findMaxValueIndex(BigDecimal[] highValues) {
        Integer maxValueIndex = 0;
        BigDecimal maxValue = highValues[0];
        for (int currentIndex = 1; currentIndex < highValues.length; currentIndex++) {
            if (highValues[currentIndex].compareTo(maxValue) >= 0) {
                maxValue = highValues[currentIndex];
                maxValueIndex = currentIndex;
            }
        }
        return maxValueIndex;
    }

    private BigDecimal[] calculateAroonDownValues() {
        Integer[] daysAfterMinValues = calculateDaysAfterMinValues();
        BigDecimal[] aroonDownValues = new BigDecimal[originalData.length];
        for (int currentIndex = 0; currentIndex < originalData.length; currentIndex++) {
            aroonDownValues[currentIndex] = calculateAroon(daysAfterMinValues[currentIndex]);
        }
        return aroonDownValues;
    }

    private Integer[] calculateDaysAfterMinValues() {
        Integer[] daysAfterMinPrice = new Integer[originalData.length];
        BigDecimal[] lowValues = extractLowValues();
        for (int currentIndex = period; currentIndex < originalData.length; currentIndex++) {
            daysAfterMinPrice[currentIndex] = calculateDaysAfterMinValue(copyOfRange(lowValues, currentIndex - period, currentIndex + 1));
        }
        return daysAfterMinPrice;
    }

    private BigDecimal[] extractLowValues() {
        return Stream.of(originalData)
                .map(Tick::getLow)
                .toArray(BigDecimal[]::new);
    }

    private Integer calculateDaysAfterMinValue(BigDecimal[] lowValues) {
        Integer minValueIndex = findMinValueIndex(lowValues);
        return period - minValueIndex;
    }

    private Integer findMinValueIndex(BigDecimal[] lowValues) {
        Integer minValueIndex = 0;
        BigDecimal minValue = lowValues[0];
        for (int currentIndex = 1; currentIndex < lowValues.length; currentIndex++) {
            if (lowValues[currentIndex].compareTo(minValue) <= 0) {
                minValue = lowValues[currentIndex];
                minValueIndex = currentIndex;
            }
        }
        return minValueIndex;
    }

    private BigDecimal calculateAroon(Integer days) {
        return nonNull(days)
                ? calculateAroonValue(days)
                : null;
    }

    private BigDecimal calculateAroonValue(Integer days) {
        BigDecimal aroonAbs = MathHelper.divide(new BigDecimal(period).subtract(new BigDecimal(days)), new BigDecimal(period));
        return nonNull(aroonAbs) ?
                aroonAbs.multiply(new BigDecimal(100))
                : null;
    }

    private void calculateAroonOscillatorValues(BigDecimal[] aroonUpValues, BigDecimal[] aroonDownValues) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new AroonResult(
                    originalData[currentIndex].getTickTime(),
                    aroonUpValues[currentIndex],
                    aroonDownValues[currentIndex],
                    calculateAroonOscillator(aroonUpValues[currentIndex], aroonDownValues[currentIndex])
            );
        }
    }

    private BigDecimal calculateAroonOscillator(BigDecimal aroonUpValue, BigDecimal aroonDownValue) {
        return nonNull(aroonUpValue) && nonNull(aroonDownValue)
                ? calculateAroonOscillatorValue(aroonUpValue, aroonDownValue)
                : null;
    }

    private BigDecimal calculateAroonOscillatorValue(BigDecimal aroonUpValue, BigDecimal aroonDownValue) {
        return aroonUpValue.subtract(aroonDownValue);
    }

}
