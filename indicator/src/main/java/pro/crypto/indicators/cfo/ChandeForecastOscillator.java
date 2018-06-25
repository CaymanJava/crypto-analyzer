package pro.crypto.indicators.cfo;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.helper.model.BigDecimalTuple;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.CFORequest;
import pro.crypto.model.result.CFOResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.CHANDE_FORECAST_OSCILLATOR;

public class ChandeForecastOscillator implements Indicator<CFOResult> {

    private final Tick[] originalData;
    private final int period;
    private final PriceType priceType;

    private CFOResult[] result;

    public ChandeForecastOscillator(CFORequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.priceType = request.getPriceType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return CHANDE_FORECAST_OSCILLATOR;
    }

    @Override
    public void calculate() {
        result = new CFOResult[originalData.length];
        BigDecimal[] averagePrices = calculateAveragePrices();
        BigDecimal[] linearRegressionValues = calculateLinearRegression(averagePrices);
        calculateChandeForecastOscillatorResult(linearRegressionValues);
    }

    @Override
    public CFOResult[] getResult() {
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
    }

    private BigDecimal[] calculateAveragePrices() {
        return IntStream.range(0, originalData.length)
                .mapToObj(this::calculateAveragePrice)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateAveragePrice(int currentIndex) {
        return currentIndex >= period - 1
                ? calculateAveragePriceValue(currentIndex)
                : null;
    }

    private BigDecimal calculateAveragePriceValue(int currentIndex) {
        return MathHelper.average(Arrays.copyOfRange(
                PriceExtractor.extractValuesByType(originalData, priceType), currentIndex - period + 1, currentIndex + 1));
    }

    private BigDecimal[] calculateLinearRegression(BigDecimal[] averagePrices) {
        BigDecimal averageCoefficient = calculateAverageCoefficient();
        return IntStream.range(0, averagePrices.length)
                .mapToObj(idx -> calculateLinearRegression(averagePrices[idx], averageCoefficient, idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateAverageCoefficient() {
        return new BigDecimal(IntStream.range(1, period + 1).average().orElse(0));
    }


    private BigDecimal calculateLinearRegression(BigDecimal averagePrice, BigDecimal averageCoefficient, int outsideIndex) {
        return nonNull(averagePrice)
                ? calculateLinearRegressionValue(averagePrice, averageCoefficient, outsideIndex)
                : null;
    }

    // slope = m = ∑((x(i) - x(avg)) * (y(i) - y(avg))) /  ∑((x(i) - x(avg))^2)
    // linearRegression = y(avg) − m * x(avg)
    private BigDecimal calculateLinearRegressionValue(BigDecimal averagePrice, BigDecimal averageCoefficient, int outsideIndex) {
        final AtomicInteger coefficient = new AtomicInteger(0);
        BigDecimalTuple divisibleDivisor = IntStream.rangeClosed(outsideIndex - period + 1, outsideIndex)
                .mapToObj(idx -> calculateDivisibleDivisor(averagePrice, averageCoefficient, coefficient, idx))
                .reduce(BigDecimalTuple.zero(), BigDecimalTuple::add);
        return calculateLinearRegressionValue(averagePrice, averageCoefficient, divisibleDivisor.getLeft(), divisibleDivisor.getRight());
    }

    private BigDecimalTuple calculateDivisibleDivisor(BigDecimal averagePrice, BigDecimal averageCoefficient,
                                                      AtomicInteger coefficient, int currentIndex) {
        BigDecimal divisible = calculateDivisible(averagePrice, averageCoefficient, coefficient.incrementAndGet(), currentIndex);
        BigDecimal divisor = calculateDivisor(coefficient.get(), averageCoefficient);
        return new BigDecimalTuple(divisible, divisor);
    }

    // (x(i) - x(avg)) * (y(i) - y(avg))
    private BigDecimal calculateDivisible(BigDecimal averagePrice, BigDecimal averageCoefficient, int coefficient, int currentIndex) {
        return new BigDecimal(coefficient).subtract(averageCoefficient).multiply(
                        originalData[currentIndex].getPriceByType(priceType).subtract(averagePrice));
    }

    // (x(i) - x(abg))^2
    private BigDecimal calculateDivisor(int coefficient, BigDecimal averageCoefficient) {
        return new BigDecimal(coefficient).subtract(averageCoefficient).pow(2);
    }

    private BigDecimal calculateLinearRegressionValue(BigDecimal averagePrice, BigDecimal averageCoefficient, BigDecimal divisible, BigDecimal divisor) {
        BigDecimal slope = MathHelper.divide(divisible, divisor);
        return nonNull(slope)
                ? averagePrice.subtract(slope.multiply(averageCoefficient))
                : null;
    }

    private void calculateChandeForecastOscillatorResult(BigDecimal[] linearRegressionValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new CFOResult(originalData[idx].getTickTime(),
                        calculateChandeForecastOscillator(linearRegressionValues[idx], originalData[idx].getPriceByType(priceType))));
    }

    private BigDecimal calculateChandeForecastOscillator(BigDecimal linearRegression, BigDecimal price) {
        return nonNull(linearRegression)
                ? calculateChandeForecastOscillatorValue(linearRegression, price)
                : null;
    }

    // CFO =(PRICE(i) − LinearRegression) * 100 / PRICE(i)
    private BigDecimal calculateChandeForecastOscillatorValue(BigDecimal linearRegression, BigDecimal price) {
        return MathHelper.divide(price.subtract(linearRegression).multiply(new BigDecimal(100)), price);
    }

}
