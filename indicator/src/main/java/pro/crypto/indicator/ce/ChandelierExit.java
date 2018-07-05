package pro.crypto.indicator.ce;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MinMaxFinder;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.indicator.atr.AverageTrueRange;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.indicator.atr.ATRRequest;
import pro.crypto.indicator.atr.ATRResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.CHANDELIER_EXIT;
import static pro.crypto.model.tick.PriceType.HIGH;
import static pro.crypto.model.tick.PriceType.LOW;

public class ChandelierExit implements Indicator<CEResult> {

    private final Tick[] originalData;
    private final int period;
    private final double factor;

    private CEResult[] result;

    public ChandelierExit(CERequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.factor = request.getFactor();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return CHANDELIER_EXIT;
    }

    @Override
    public void calculate() {
        result = new CEResult[originalData.length];
        BigDecimal[] averageTrueRangeValues = calculateAverageTrueRangeValues();
        BigDecimal[] longExits = calculateLongExits(averageTrueRangeValues);
        BigDecimal[] shortExits = calculateShortExits(averageTrueRangeValues);
        buildChandelierResults(longExits, shortExits);
    }

    @Override
    public CEResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
        checkFactor();
    }

    private void checkFactor() {
        if (factor < 0) {
            throw new WrongIncomingParametersException(format(ENGLISH, "Chandelier exit factor should be more than 0 {indicator: {%s}, shift: {%.2f}}",
                    getType().toString(), factor));
        }
    }

    private BigDecimal[] calculateAverageTrueRangeValues() {
        return IndicatorResultExtractor.extract(calculateAverageTrueRange());
    }

    private ATRResult[] calculateAverageTrueRange() {
        return new AverageTrueRange(buildATRRequest()).getResult();
    }

    private ATRRequest buildATRRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(period)
                .build();
    }

    private BigDecimal[] calculateLongExits(BigDecimal[] averageTrueRangeValues) {
        final BigDecimal[] maxValues = calculateMaxValues();
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateExit(averageTrueRangeValues[idx], maxValues[idx], idx, BigDecimal::subtract))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] calculateMaxValues() {
        return MinMaxFinder.findMaxValues(PriceExtractor.extractValuesByType(originalData, HIGH), period);
    }

    private BigDecimal[] calculateShortExits(BigDecimal[] averageTrueRangeValues) {
        BigDecimal[] minValues = calculateMinValues();
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateExit(averageTrueRangeValues[idx], minValues[idx], idx, BigDecimal::add))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] calculateMinValues() {
        return MinMaxFinder.findMinValues(PriceExtractor.extractValuesByType(originalData, LOW), period);
    }

    private BigDecimal calculateExit(BigDecimal averageTrueRangeValue, BigDecimal minValue, int currentIndex,
                                     BiFunction<BigDecimal, BigDecimal, BigDecimal> exitFunction) {
        return isPossibleToCalculate(averageTrueRangeValue, minValue, currentIndex)
                ? exitFunction.apply(minValue, averageTrueRangeValue.multiply(new BigDecimal(factor)))
                : null;
    }

    private boolean isPossibleToCalculate(BigDecimal averageTrueRangeValue, BigDecimal value, int currentIndex) {
        return currentIndex >= period - 1 && nonNull(averageTrueRangeValue) && nonNull(value);
    }

    private void buildChandelierResults(BigDecimal[] longExits, BigDecimal[] shortExits) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new CEResult(originalData[idx].getTickTime(), longExits[idx], shortExits[idx]));
    }

}
