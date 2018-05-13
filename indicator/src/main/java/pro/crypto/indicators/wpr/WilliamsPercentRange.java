package pro.crypto.indicators.wpr;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MinMaxCalculator;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.WPRRequest;
import pro.crypto.model.result.WPRResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.WILLIAMS_PERCENT_RANGE;

public class WilliamsPercentRange implements Indicator<WPRResult> {

    private final Tick[] originalData;
    private final int period;

    private WPRResult[] result;

    public WilliamsPercentRange(WPRRequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return WILLIAMS_PERCENT_RANGE;
    }

    @Override
    public void calculate() {
        result = new WPRResult[originalData.length];
        BigDecimal[] maxValues = MinMaxCalculator.calculateMaximumValues(extractHighPrices(), period);
        BigDecimal[] minValues = MinMaxCalculator.calculateMinimumValues(extractLowPrices(), period);
        calculateWilliamsPercentRange(maxValues, minValues);
    }

    @Override
    public WPRResult[] getResult() {
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

    private BigDecimal[] extractHighPrices() {
        return Stream.of(originalData)
                .map(Tick::getHigh)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] extractLowPrices() {
        return Stream.of(originalData)
                .map(Tick::getLow)
                .toArray(BigDecimal[]::new);
    }

    private void calculateWilliamsPercentRange(BigDecimal[] maxValues, BigDecimal[] minValues) {
        for (int i = 0; i < result.length; i++) {
            result[i] = nonNull(maxValues[i]) && nonNull(minValues[i])
                    ? new WPRResult(originalData[i].getTickTime(), calculateWilliamsPercentRangeValue(maxValues[i], minValues[i], originalData[i].getClose()))
                    : new WPRResult(originalData[i].getTickTime(), null);
        }
    }

    private BigDecimal calculateWilliamsPercentRangeValue(BigDecimal maxValue, BigDecimal minValue, BigDecimal close) {
        BigDecimal williamsRange = MathHelper.divide(maxValue.subtract(close), maxValue.subtract(minValue));
        return nonNull(williamsRange)
                ? williamsRange.multiply(new BigDecimal(-100))
                : null;
    }

}