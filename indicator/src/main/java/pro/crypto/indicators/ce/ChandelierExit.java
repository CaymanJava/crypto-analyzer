package pro.crypto.indicators.ce;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MinMaxFinder;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.indicators.atr.AverageTrueRange;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ATRRequest;
import pro.crypto.model.request.CERequest;
import pro.crypto.model.result.ATRResult;
import pro.crypto.model.result.CEResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

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
        BigDecimal[] longExits = new BigDecimal[originalData.length];
        BigDecimal[] maxValues = calculateMaxValues();
        for (int currentIndex = period - 1; currentIndex < longExits.length; currentIndex++) {
            longExits[currentIndex] = calculateLongExit(averageTrueRangeValues[currentIndex], maxValues[currentIndex]);
        }
        return longExits;
    }

    private BigDecimal[] calculateMaxValues() {
        return MinMaxFinder.findMaxValues(PriceExtractor.extractValuesByType(originalData, HIGH), period);
    }

    private BigDecimal calculateLongExit(BigDecimal averageTrueRangeValue, BigDecimal maxValue) {
        return nonNull(averageTrueRangeValue) && nonNull(maxValue)
                ? calculateLongExitValue(averageTrueRangeValue, maxValue)
                : null;
    }

    private BigDecimal calculateLongExitValue(BigDecimal averageTrueRangeValue, BigDecimal maxValue) {
        return maxValue.subtract(averageTrueRangeValue.multiply(new BigDecimal(factor)));
    }

    private BigDecimal[] calculateShortExits(BigDecimal[] averageTrueRangeValues) {
        BigDecimal[] shortExits = new BigDecimal[originalData.length];
        BigDecimal[] minValues = calculateMinValues();
        for (int currentIndex = period - 1; currentIndex < shortExits.length; currentIndex++) {
            shortExits[currentIndex] = calculateShortExit(averageTrueRangeValues[currentIndex], minValues[currentIndex]);
        }
        return shortExits;
    }

    private BigDecimal[] calculateMinValues() {
        return MinMaxFinder.findMinValues(PriceExtractor.extractValuesByType(originalData, LOW), period);
    }

    private BigDecimal calculateShortExit(BigDecimal averageTrueRangeValue, BigDecimal minValue) {
        return nonNull(averageTrueRangeValue) && nonNull(minValue)
                ? calculateShortExitValue(averageTrueRangeValue, minValue)
                : null;
    }

    private BigDecimal calculateShortExitValue(BigDecimal averageTrueRangeValue, BigDecimal minValue) {
        return minValue.add(averageTrueRangeValue.multiply(new BigDecimal(factor)));
    }

    private void buildChandelierResults(BigDecimal[] longExits, BigDecimal[] shortExits) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new CEResult(
                    originalData[currentIndex].getTickTime(),
                    longExits[currentIndex],
                    shortExits[currentIndex]
            );
        }
    }

}
