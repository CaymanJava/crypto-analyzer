package pro.crypto.indicator.ma;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;
import static pro.crypto.model.indicator.IndicatorType.MODIFIED_MOVING_AVERAGE;

public class ModifiedMovingAverage extends MovingAverage {

    private final Tick[] originalData;
    private final int period;

    ModifiedMovingAverage(Tick[] originalData, int period, PriceType priceType) {
        checkIncomingData(originalData, period, priceType);
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
    }

    @Override
    public IndicatorType getType() {
        return MODIFIED_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        initResultArray(this.originalData.length);
        fillInInitialPositions(originalData, period);
        fillInInitialIndicatorValue();
        fillInRemainPositions();
    }

    private void fillInInitialIndicatorValue() {
        calculateSimpleAverage(0, period - 1, originalData);
    }

    private void fillInRemainPositions() {
        IntStream.range(period, result.length)
                .forEach(this::setMAResult);
    }

    private void setMAResult(int currentIndex) {
        result[currentIndex] = new MAResult(originalData[currentIndex].getTickTime(), calculateIndicatorValue(currentIndex));
    }

    //MMAt = MMAt-1 + (Pt - MMAt-1)/n
    private BigDecimal calculateIndicatorValue(int currentIndex) {
        BigDecimal firstTermin = MathHelper.divide(
                originalData[currentIndex].getPriceByType(priceType).subtract(result[currentIndex - 1].getIndicatorValue()),
                new BigDecimal(period));
        return nonNull(firstTermin)
                ? result[currentIndex - 1].getIndicatorValue().add(firstTermin)
                : null;
    }

}
