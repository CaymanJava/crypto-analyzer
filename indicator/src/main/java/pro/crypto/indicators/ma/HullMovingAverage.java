package pro.crypto.indicators.ma;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static pro.crypto.model.IndicatorType.HULL_MOVING_AVERAGE;

public class HullMovingAverage extends MovingAverage {

    private final Tick[] originalData;
    private final int period;

    HullMovingAverage(Tick[] originalData, int period, PriceType priceType) {
        checkIncomingData(originalData, period, priceType);
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
    }

    @Override
    public IndicatorType getType() {
        return HULL_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        initResultArray(this.originalData.length);
        fillInInitialPositions(originalData, period);
        fillInRemainPositions();
    }

    private void fillInRemainPositions() {
        for (int currentIndex = period - 1; currentIndex < originalData.length; currentIndex++) {
            result[currentIndex] = buildMovingAverageResult(currentIndex, countIndicatorValue(currentIndex));
        }
    }

    private BigDecimal countIndicatorValue(int currentIndex) {
        BigDecimal simpleAverageFromPeriod = countAndGetSimpleAverage(currentIndex - period + 1, currentIndex, originalData, period);
        BigDecimal simpleAverageFromDividedTwoPeriod = countAndGetSimpleAverage(currentIndex - period/2 + 1, currentIndex, originalData, period/2);
        return simpleAverageFromDividedTwoPeriod.subtract(simpleAverageFromPeriod).add(simpleAverageFromDividedTwoPeriod);
    }

    private MAResult buildMovingAverageResult(int currentIndex, BigDecimal indicatorValue) {
        return new MAResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRoundValue(originalData[currentIndex].getPriceByType(priceType)),
                MathHelper.scaleAndRoundValue(indicatorValue)
        );
    }

}
