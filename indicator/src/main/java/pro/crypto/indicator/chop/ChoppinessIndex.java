package pro.crypto.indicator.chop;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MinMaxFinder;
import pro.crypto.helper.PriceVolumeExtractor;
import pro.crypto.indicator.atr.ATRRequest;
import pro.crypto.indicator.atr.AverageTrueRange;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.CHOPPINESS_INDEX;
import static pro.crypto.model.tick.PriceType.HIGH;
import static pro.crypto.model.tick.PriceType.LOW;

public class ChoppinessIndex implements Indicator<CHOPResult> {

    private final Tick[] originalData;
    private final int period;

    private CHOPResult[] result;

    public ChoppinessIndex(IndicatorRequest creationRequest) {
        CHOPRequest request = (CHOPRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return CHOPPINESS_INDEX;
    }

    @Override
    public void calculate() {
        result = new CHOPResult[originalData.length];
        BigDecimal[] atrSumValues = calculateATRSumValues();
        BigDecimal[] maxValues = MinMaxFinder.findMaxValues(PriceVolumeExtractor.extract(originalData, HIGH), period);
        BigDecimal[] minValues = MinMaxFinder.findMinValues(PriceVolumeExtractor.extract(originalData, LOW), period);
        calculateChoppinessIndexResult(atrSumValues, maxValues, minValues);
    }

    @Override
    public CHOPResult[] getResult() {
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

    private BigDecimal[] calculateATRSumValues() {
        BigDecimal[] atrValues = IndicatorResultExtractor.extractIndicatorValues(calculateAverageTrueRange());
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateATRSum(atrValues, idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateATRSum(BigDecimal[] atrValues, int currentIndex) {
        return currentIndex >= period - 1
                ? calculateATRSumValue(atrValues, currentIndex)
                : null;
    }

    private BigDecimal calculateATRSumValue(BigDecimal[] atrValues, int currentIndex) {
        return MathHelper.sum(Arrays.copyOfRange(atrValues, currentIndex - period + 1, currentIndex + 1));
    }

    private SimpleIndicatorResult[] calculateAverageTrueRange() {
        return new AverageTrueRange(buildATRRequest()).getResult();
    }

    private IndicatorRequest buildATRRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(1)
                .build();
    }

    private void calculateChoppinessIndexResult(BigDecimal[] atrSumValues, BigDecimal[] maxValues, BigDecimal[] minValues) {
        BigDecimal log10Period = MathHelper.log(new BigDecimal(period), 10);
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new CHOPResult(
                        originalData[idx].getTickTime(),
                        calculateChoppinessIndex(atrSumValues[idx], maxValues[idx], minValues[idx], log10Period)));
    }

    private BigDecimal calculateChoppinessIndex(BigDecimal atrSumValue, BigDecimal maxValue, BigDecimal minValue, BigDecimal log10Period) {
        return nonNull(atrSumValue) && nonNull(maxValue) && nonNull(minValue)
                ? calculateChoppinessIndexValue(atrSumValue, maxValue, minValue, log10Period)
                : null;
    }

    // 100 * LOG10( SUM(ATR(1), n) / ( MaxHigh(n) - MinLow(n) ) ) / LOG10(n)
    private BigDecimal calculateChoppinessIndexValue(BigDecimal atrSumValue, BigDecimal maxValue, BigDecimal minValue, BigDecimal log10Period) {
        BigDecimal quotient = calculateQuotient(atrSumValue, maxValue, minValue);
        return MathHelper.divide(
                new BigDecimal(100)
                        .multiply(MathHelper.log(quotient, 10)),
                log10Period);
    }

    private BigDecimal calculateQuotient(BigDecimal atrSumValue, BigDecimal maxValue, BigDecimal minValue) {
        return MathHelper.divide(atrSumValue, maxValue.subtract(minValue));
    }

}
