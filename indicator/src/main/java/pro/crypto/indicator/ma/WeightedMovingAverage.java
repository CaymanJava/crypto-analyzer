package pro.crypto.indicator.ma;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

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
        IntStream.range(period - 1, originalData.length)
                .forEach(idx -> buildMovingAverageResult(weightedCoefficientSum, idx));
    }

    private void buildMovingAverageResult(int weightedCoefficientSum, int currentIndex) {
        BigDecimal indicatorValue = calculateIndicatorValue(weightedCoefficientSum, currentIndex);
        result[currentIndex] = buildMovingAverageResult(currentIndex, indicatorValue);
    }

    private int calculateWeightedCoefficientSum() {
        return range(1, period + 1).reduce(0, (a, b) -> a + b);
    }

    private BigDecimal calculateIndicatorValue(int weightedCoefficientSum, int currentIndex) {
        BigDecimal weightedPriceSum = calculateWeightedPriceSum(currentIndex);
        return MathHelper.divide(weightedPriceSum, new BigDecimal(weightedCoefficientSum));
    }

    private BigDecimal calculateWeightedPriceSum(int currentIndex) {
        AtomicInteger currentWeight = new AtomicInteger(1);
        return IntStream.rangeClosed(currentIndex - period + 1, currentIndex)
                .mapToObj(idx -> originalData[idx].getPriceByType(priceType).multiply(new BigDecimal(currentWeight.getAndIncrement())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private MAResult buildMovingAverageResult(int currentIndex, BigDecimal indicatorValue) {
        return new MAResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRound(indicatorValue)
        );
    }

}
