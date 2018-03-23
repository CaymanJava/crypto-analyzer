package pro.crypto.indicators.ma;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

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
        int weightedCoefficientSum = countWeightedCoefficientSum();

        for (int currentIndex = period - 1; currentIndex < originalData.length; currentIndex++) {
            BigDecimal indicatorValue = countIndicatorValue(weightedCoefficientSum, currentIndex);
            result[currentIndex] = buildMovingAverageResult(currentIndex, indicatorValue);
        }
    }

    private int countWeightedCoefficientSum() {
        int weightedSum = 0;
        for (int i = 1; i <= period; i++) {
            weightedSum += i;
        }
        return weightedSum;
    }

    private BigDecimal countIndicatorValue(int weightedCoefficientSum, int currentIndex) {
        BigDecimal weightedPriceSum = countWeightedPriceSum(currentIndex);
        return MathHelper.divide(weightedPriceSum, new BigDecimal(weightedCoefficientSum));
    }

    private BigDecimal countWeightedPriceSum(int currentIndex) {
        int currentWeight = 1;
        BigDecimal weightedPriceSum = new BigDecimal(0);
        for (int i = currentIndex - period + 1; i <= currentIndex; i++) {
            weightedPriceSum = weightedPriceSum.add(originalData[i].getPriceByType(priceType).multiply(new BigDecimal(currentWeight)));
            currentWeight++;
        }
        return weightedPriceSum;
    }

    private MAResult buildMovingAverageResult(int currentIndex, BigDecimal indicatorValue) {
        return new MAResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRoundValue(originalData[currentIndex].getPriceByType(priceType)),
                MathHelper.scaleAndRoundValue(indicatorValue)
        );
    }

}
