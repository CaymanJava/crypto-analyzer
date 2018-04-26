package pro.crypto.indicators.roc;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ROCRequest;
import pro.crypto.model.result.ROCResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.RANGE_OF_CHANGE;

public class RangeOfChange implements Indicator<ROCResult> {

    private final Tick[] originalData;
    private final int period;

    private ROCResult[] result;

    public RangeOfChange(ROCRequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return RANGE_OF_CHANGE;
    }

    @Override
    public void calculate() {
        result = new ROCResult[originalData.length];
        calculateRangeOfChangeValues();
    }

    @Override
    public ROCResult[] getResult() {
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

    private void calculateRangeOfChangeValues() {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = buildRocResult(currentIndex);
        }
    }

    private ROCResult buildRocResult(int currentIndex) {
        return new ROCResult(
                originalData[currentIndex].getTickTime(),
                calculateRangeOfChange(currentIndex)
        );
    }

    private BigDecimal calculateRangeOfChange(int currentIndex) {
        if (currentIndex >= period - 1) {
            return calculateRangeOfChangeValue(currentIndex);
        }
        return null;
    }

    private BigDecimal calculateRangeOfChangeValue(int currentIndex) {
        BigDecimal rangeOfChangeAbs = MathHelper.divide(originalData[currentIndex].getClose()
                        .subtract(originalData[currentIndex - period + 1].getClose()), originalData[currentIndex - period + 1].getClose());
        return nonNull(rangeOfChangeAbs)
                ? rangeOfChangeAbs.multiply(new BigDecimal(100))
                : null;
    }

}
