package pro.crypto.indicators.ma;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.stream.IntStream.range;
import static pro.crypto.model.IndicatorType.WEIGHTED_MOVING_AVERAGE;

public class WeightedMovingAverage extends MovingAverage {

    private final Tick[] originalData;
    private final int period;

    WeightedMovingAverage(Tick[] originalData, int period, PriceType priceType) {
        checkIncomingData(originalData, period, priceType);
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
    }

    @Override
    public IndicatorType getType() {
        return WEIGHTED_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        initResultArray(originalData.length);
        fillInInitialPositions(originalData, period);
        fillInRemainPositions();
    }

    private void fillInRemainPositions() {
        int weightedCoefficientSum = calculateWeightedCoefficientSum();

        for (int currentIndex = period - 1; currentIndex < originalData.length; currentIndex++) {
            BigDecimal indicatorValue = calculateIndicatorValue(weightedCoefficientSum, currentIndex);
            result[currentIndex] = buildMovingAverageResult(currentIndex, indicatorValue);
        }
    }

    private int calculateWeightedCoefficientSum() {
        return range(1, period + 1).reduce(0, (a, b) -> a + b);
    }

    private BigDecimal calculateIndicatorValue(int weightedCoefficientSum, int currentIndex) {
        BigDecimal weightedPriceSum = calculateWeightedPriceSum(currentIndex);
        return MathHelper.divide(weightedPriceSum, new BigDecimal(weightedCoefficientSum));
    }

    private BigDecimal calculateWeightedPriceSum(int currentIndex) {
        int currentWeight = 1;
        BigDecimal weightedPriceSum = BigDecimal.ZERO;
        for (int i = currentIndex - period + 1; i <= currentIndex; i++) {
            weightedPriceSum = weightedPriceSum.add(originalData[i].getPriceByType(priceType).multiply(new BigDecimal(currentWeight)));
            currentWeight++;
        }
        return weightedPriceSum;
    }

    private MAResult buildMovingAverageResult(int currentIndex, BigDecimal indicatorValue) {
        return new MAResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRound(originalData[currentIndex].getPriceByType(priceType)),
                MathHelper.scaleAndRound(indicatorValue)
        );
    }

}
