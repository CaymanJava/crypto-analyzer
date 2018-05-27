package pro.crypto.indicators.chop;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MinMaxCalculator;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.indicators.atr.AverageTrueRange;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ATRRequest;
import pro.crypto.model.request.CHOPRequest;
import pro.crypto.model.result.ATRResult;
import pro.crypto.model.result.CHOPResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.CHOPPINESS_INDEX;
import static pro.crypto.model.tick.PriceType.HIGH;
import static pro.crypto.model.tick.PriceType.LOW;

public class ChoppinessIndex implements Indicator<CHOPResult> {

    private final Tick[] originalData;
    private final int period;

    private CHOPResult[] result;

    public ChoppinessIndex(CHOPRequest request) {
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
        BigDecimal[] maxValues = MinMaxCalculator.calculateMaxValues(PriceExtractor.extractValuesByType(originalData, HIGH), period);
        BigDecimal[] minValues = MinMaxCalculator.calculateMinValues(PriceExtractor.extractValuesByType(originalData, LOW), period);
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
        BigDecimal[] atrValues = IndicatorResultExtractor.extract(calculateAverageTrueRange());
        BigDecimal[] atrSumValues = new BigDecimal[originalData.length];
        for (int currentIndex = period - 1; currentIndex < atrSumValues.length; currentIndex++) {
            atrSumValues[currentIndex] = MathHelper.sum(Arrays.copyOfRange(atrValues, currentIndex - period + 1, currentIndex + 1));
        }
        return atrSumValues;
    }

    private ATRResult[] calculateAverageTrueRange() {
        return new AverageTrueRange(buildATRRequest()).getResult();
    }

    private ATRRequest buildATRRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(1)
                .build();
    }

    private void calculateChoppinessIndexResult(BigDecimal[] atrSumValues, BigDecimal[] maxValues, BigDecimal[] minValues) {
        BigDecimal log10Period = MathHelper.log(new BigDecimal(period), 10);
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new CHOPResult(
                    originalData[currentIndex].getTickTime(),
                    calculateChoppinessIndex(atrSumValues[currentIndex], maxValues[currentIndex], minValues[currentIndex], log10Period));
        }
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
