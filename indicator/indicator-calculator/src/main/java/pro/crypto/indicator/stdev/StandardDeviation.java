package pro.crypto.indicator.stdev;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.STANDARD_DEVIATION;

/**
 * Indicator is made for visual analyzing and signal line crossing
 */
public class StandardDeviation implements Indicator<StDevResult> {

    private final Tick[] originalData;
    private final PriceType priceType;
    private final IndicatorType movingAverageType;
    private final int period;

    private StDevResult[] result;

    public StandardDeviation(IndicatorRequest creationRequest) {
        StDevRequest request = (StDevRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.priceType = request.getPriceType();
        this.movingAverageType = ofNullable(request.getMovingAverageType()).orElse(SIMPLE_MOVING_AVERAGE);
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
        return IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverage());
    }

    private SimpleIndicatorResult[] calculateMovingAverage() {
        return MovingAverageFactory.create(buildMovingAverageRequest()).getResult();
    }

    private IndicatorRequest buildMovingAverageRequest() {
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
