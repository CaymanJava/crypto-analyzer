package pro.crypto.indicator.roc;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.RATE_OF_CHANGE;

public class RateOfChange implements Indicator<ROCResult> {

    private final Tick[] originalData;
    private final int period;
    private final PriceType priceType;

    private ROCResult[] result;

    public RateOfChange(IndicatorRequest creationRequest) {
        ROCRequest request = (ROCRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.priceType = request.getPriceType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return RATE_OF_CHANGE;
    }

    @Override
    public void calculate() {
        result = new ROCResult[originalData.length];
        calculateRateOfChangeValues();
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
        checkPriceType(priceType);
    }

    private void calculateRateOfChangeValues() {
        IntStream.range(0, result.length)
                .forEach(this::setROCResult);
    }

    private void setROCResult(int currentIndex) {
        result[currentIndex] = buildRocResult(currentIndex);
    }

    private ROCResult buildRocResult(int currentIndex) {
        return new ROCResult(
                originalData[currentIndex].getTickTime(),
                calculateRateOfChange(currentIndex)
        );
    }

    private BigDecimal calculateRateOfChange(int currentIndex) {
        return currentIndex >= period
                ? calculateRateOfChangeValue(currentIndex)
                : null;
    }

    private BigDecimal calculateRateOfChangeValue(int currentIndex) {
        return MathHelper.divide(originalData[currentIndex].getPriceByType(priceType)
                        .subtract(originalData[currentIndex - period].getPriceByType(priceType)).multiply(new BigDecimal(100)),
                originalData[currentIndex - period].getPriceByType(priceType));
    }

}
