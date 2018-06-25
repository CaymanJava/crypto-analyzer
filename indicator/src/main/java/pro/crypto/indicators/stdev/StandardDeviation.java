package pro.crypto.indicators.stdev;

import pro.crypto.helper.IndicatorResultExtractor;
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
import java.util.stream.IntStream;

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
        return IndicatorResultExtractor.extract(calculateMovingAverage());
    }

    private MAResult[] calculateMovingAverage() {
        return MovingAverageFactory.create(buildMovingAverageRequest()).getResult();
    }

    private MARequest buildMovingAverageRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .indicatorType(movingAverageType)
                .period(period)
                .priceType(priceType)
                .build();
    }

    // Σ (x - MAResult)^2
    private BigDecimal[] calculateDivisibleValues(BigDecimal[] averagePrices) {
        return IntStream.range(0, averagePrices.length)
                .mapToObj(idx -> calculateSumOfDifferenceBetweenPriceAndMovingAverage(averagePrices[idx], idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateSumOfDifferenceBetweenPriceAndMovingAverage(BigDecimal averagePrice, int currentIndex) {
        return isNull(averagePrice)
                ? null
                : IntStream.range(currentIndex - period + 1, currentIndex + 1)
                .mapToObj(idx -> calculateDifference(averagePrice, idx).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDifference(BigDecimal averagePrice, int index) {
        return originalData[index].getPriceByType(priceType).subtract(averagePrice);
    }

    private void calculateStandardDeviation(BigDecimal[] divisibleValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = calculateStandardDeviationValue(divisibleValues[idx], idx));
    }

    private StDevResult calculateStandardDeviationValue(BigDecimal divisibleValue, int currentIndex) {
        return isNull(divisibleValue)
                ? new StDevResult(originalData[currentIndex].getTickTime(), null)
                : new StDevResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRound(MathHelper.sqrt(MathHelper.divide(divisibleValue, getDivisor()))));
    }

    private BigDecimal getDivisor() {
        return new BigDecimal(period >= 30 ? period - 1 : period);
    }

}
