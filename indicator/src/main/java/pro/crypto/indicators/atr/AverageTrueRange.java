package pro.crypto.indicators.atr;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.TrueRangeCounter;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ATRRequest;
import pro.crypto.model.result.ATRResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;

public class AverageTrueRange implements Indicator<ATRResult> {

    private final Tick[] originalData;
    private final int period;

    private ATRResult[] result;

    public AverageTrueRange(ATRRequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return AVERAGE_TRUE_RANGE;
    }

    @Override
    public void calculate() {
        result = new ATRResult[originalData.length];
        BigDecimal[] trueRangeValues = TrueRangeCounter.countTrueRangeValues(originalData);
        countAverageTrueRangeValues(trueRangeValues);
    }

    @Override
    public ATRResult[] getResult() {
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

    private void countAverageTrueRangeValues(BigDecimal[] trueRangeValues) {
        fillInInitialValues();
        fillInFirstValue(trueRangeValues);
        fillInRemainValues(trueRangeValues);
    }

    private void fillInInitialValues() {
        for (int i = 0; i < period - 2; i++) {
            result[i] = new ATRResult(originalData[i].getTickTime(), null);
        }
    }

    private void fillInFirstValue(BigDecimal[] trueRangeValues) {
        result[period - 2] = new ATRResult(
                originalData[period - 2].getTickTime(),
                MathHelper.average(Arrays.copyOfRange(trueRangeValues, 0, period - 1)));
    }

    private void fillInRemainValues(BigDecimal[] trueRangeValues) {
        for (int currentIndex = period - 1; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = countAverageTrueRange(trueRangeValues[currentIndex], currentIndex);
        }
    }

    // ATRk = (ATRk-1 * (n - 1) + TRk) / n
    private ATRResult countAverageTrueRange(BigDecimal trueRangeValue, int currentIndex) {
        return new ATRResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.divide(
                        result[currentIndex - 1].getIndicatorValue()
                                .multiply(new BigDecimal(period - 1))
                                .add(trueRangeValue),
                        new BigDecimal(period))
        );
    }

}
