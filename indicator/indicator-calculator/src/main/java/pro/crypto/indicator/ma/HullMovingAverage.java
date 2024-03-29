package pro.crypto.indicator.ma;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static pro.crypto.model.indicator.IndicatorType.HULL_MOVING_AVERAGE;

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
        IntStream.range(period - 1, originalData.length)
                .forEach(idx -> result[idx] = buildMovingAverageResult(idx, calculateIndicatorValue(idx)));
    }

    private BigDecimal calculateIndicatorValue(int currentIndex) {
        BigDecimal simpleAverageFromPeriod = calculateAndGetSimpleAverage(currentIndex - period + 1, currentIndex, originalData);
        BigDecimal simpleAverageFromDividedTwoPeriod = calculateAndGetSimpleAverage(currentIndex - period / 2 + 1, currentIndex, originalData);
        return simpleAverageFromDividedTwoPeriod.subtract(simpleAverageFromPeriod).add(simpleAverageFromDividedTwoPeriod);
    }

    private MAResult buildMovingAverageResult(int currentIndex, BigDecimal indicatorValue) {
        return new MAResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRound(indicatorValue)
        );
    }

}
