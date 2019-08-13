package pro.crypto.indicator.ma;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static pro.crypto.model.indicator.IndicatorType.SMOOTHED_MOVING_AVERAGE;

public class SmoothedMovingAverage extends MovingAverage {

    private final Tick[] originalData;
    private final int period;

    SmoothedMovingAverage(Tick[] originalData, int period, PriceType priceType) {
        checkIncomingData(originalData, period, priceType);
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
    }

    @Override
    public IndicatorType getType() {
        return SMOOTHED_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        initResultArray(originalData.length);
        fillInInitialPositions(originalData, period);
        fillInStartIndicatorValue();
        fillInRemainPositions();
    }

    private void fillInRemainPositions() {
        IntStream.range(period, originalData.length)
                .forEach(this::setMovingAverageResult);
    }

    private void setMovingAverageResult(int currentIndex) {
        result[currentIndex] = buildMovingAverageResult(currentIndex);
    }

    private MAResult buildMovingAverageResult(int currentIndex) {
        return new MAResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRound(calculateSmoothedAverage(currentIndex)));
    }

    private BigDecimal calculateSmoothedAverage(int currentIndex) {
        return MathHelper.divide((result[currentIndex - 1].getIndicatorValue().multiply(new BigDecimal(period - 1)))
                        .add(originalData[currentIndex].getPriceByType(priceType)),
                new BigDecimal(period));
    }

    private void fillInStartIndicatorValue() {
        calculateSimpleAverage(0, period - 1, originalData);
    }

}
