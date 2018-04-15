package pro.crypto.indicators.stdev;

import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.request.StDevRequest;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.result.StDevResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.STANDARD_DEVIATION;

public class StandardDeviation implements Indicator<StDevResult> {

    private final Tick[] originalData;
    private final PriceType priceType;
    private final IndicatorType movingAverageType;
    private final int period;

    private StDevResult[] result;

    public StandardDeviation(StDevRequest request) {
        this.originalData = request.getOriginalData();
        this.priceType = request.getPriceType();
        this.movingAverageType = isNull(request.getMovingAverageType()) ? SIMPLE_MOVING_AVERAGE : request.getMovingAverageType();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return STANDARD_DEVIATION;
    }

    @Override
    public void calculate() {
        result = new StDevResult[originalData.length];
        BigDecimal[] averagePrices = calculateMovingAveragePrices();
        BigDecimal[] divisibleValues = calculateDivisibleValues(averagePrices);
        calculateStandardDeviation(divisibleValues);
    }

    @Override
    public StDevResult[] getResult() {
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
        checkMovingAverageType(movingAverageType);
    }

    private BigDecimal[] calculateMovingAveragePrices() {
        return Stream.of(MovingAverageFactory.create(buildSignalLineMovingAverageRequest()).getResult())
                .map(MAResult::getIndicatorValue)
                .toArray(BigDecimal[]::new);
    }

    private MARequest buildSignalLineMovingAverageRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .indicatorType(movingAverageType)
                .period(period)
                .priceType(priceType)
                .build();
    }

    // Σ (x - MAResult)^2
    private BigDecimal[] calculateDivisibleValues(BigDecimal[] averagePrices) {
        BigDecimal[] sumValues = new BigDecimal[averagePrices.length];
        for (int currentIndex = 0; currentIndex < sumValues.length; currentIndex++) {
            sumValues[currentIndex] = calculateSumOfDifferenceBetweenPriceAndMovingAverage(averagePrices[currentIndex], currentIndex);
        }
        return sumValues;
    }

    private BigDecimal calculateSumOfDifferenceBetweenPriceAndMovingAverage(BigDecimal averagePrice, int currentIndex) {
        if (isNull(averagePrice)) return null;
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = currentIndex - period + 1; i < currentIndex + 1; i++) {
            sum = sum.add(calculateDifference(averagePrice, i).pow(2));
        }
        return sum;
    }

    private BigDecimal calculateDifference(BigDecimal averagePrice, int index) {
        return originalData[index].getPriceByType(priceType).subtract(averagePrice);
    }

    private void calculateStandardDeviation(BigDecimal[] divisibleValues) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = calculateStandardDeviationValue(divisibleValues[currentIndex], currentIndex);
        }
    }

    private StDevResult calculateStandardDeviationValue(BigDecimal divisibleValue, int currentIndex) {
        if (isNull(divisibleValue)) return new StDevResult(originalData[currentIndex].getTickTime(), null);
        return new StDevResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRound(MathHelper.sqrt(MathHelper.divide(divisibleValue, getDivisor())))
        );
    }

    private BigDecimal getDivisor() {
        return new BigDecimal(period >= 30 ? period - 1 : period);
    }

}
