package pro.crypto.indicators.wpr;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MinMaxFinder;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.WPRRequest;
import pro.crypto.model.result.WPRResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.WILLIAMS_PERCENT_RANGE;
import static pro.crypto.model.tick.PriceType.HIGH;
import static pro.crypto.model.tick.PriceType.LOW;

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
        BigDecimal[] maxValues = MinMaxFinder.findMaxValues(PriceExtractor.extractValuesByType(originalData, HIGH), period);
        BigDecimal[] minValues = MinMaxFinder.findMinValues(PriceExtractor.extractValuesByType(originalData, LOW), period);
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

    private void calculateWilliamsPercentRange(BigDecimal[] maxValues, BigDecimal[] minValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> buildWPRResult(maxValues, minValues, idx))
                .toArray(WPRResult[]::new);
    }

    private WPRResult buildWPRResult(BigDecimal[] maxValues, BigDecimal[] minValues, int currentIndex) {
        return nonNull(maxValues[currentIndex]) && nonNull(minValues[currentIndex])
                ? new WPRResult(originalData[currentIndex].getTickTime(), calculateWilliamsPercentRangeValue(maxValues[currentIndex], minValues[currentIndex], originalData[currentIndex].getClose()))
                : new WPRResult(originalData[currentIndex].getTickTime(), null);
    }

    private BigDecimal calculateWilliamsPercentRangeValue(BigDecimal maxValue, BigDecimal minValue, BigDecimal close) {
        BigDecimal williamsRange = MathHelper.divide(maxValue.subtract(close), maxValue.subtract(minValue));
        return nonNull(williamsRange)
                ? williamsRange.multiply(new BigDecimal(-100))
                : null;
    }

}
