package pro.crypto.indicators.cfo;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.CFORequest;
import pro.crypto.model.result.CFOResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;
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
        BigDecimal[] averagePrices = new BigDecimal[originalData.length];
        for (int currentIndex = period - 1; currentIndex < averagePrices.length; currentIndex++) {
            averagePrices[currentIndex] = MathHelper.average(Arrays.copyOfRange(
                    PriceExtractor.extractValuesByType(originalData, priceType), currentIndex - period + 1, currentIndex + 1));
        }
        return averagePrices;
    }

    private BigDecimal[] calculateLinearRegression(BigDecimal[] averagePrices) {
        BigDecimal[] linearRegressionValues = new BigDecimal[averagePrices.length];
        BigDecimal averageCoefficient = calculateAverageCoefficient();
        for (int currentIndex = 0; currentIndex < linearRegressionValues.length; currentIndex++) {
            linearRegressionValues[currentIndex] = calculateLinearRegression(averagePrices[currentIndex], averageCoefficient, currentIndex);
        }
        return linearRegressionValues;
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
        BigDecimal divisible = BigDecimal.ZERO;
        BigDecimal divisor = BigDecimal.ZERO;
        int coefficient = 1;
        for (int currentIndex = outsideIndex - period + 1; currentIndex <= outsideIndex; currentIndex++) {
            divisible = divisible.add(calculateDivisible(averagePrice, averageCoefficient, coefficient, currentIndex));
            divisor = divisor.add(calculateDivisor(coefficient, averageCoefficient));
            coefficient++;
        }
        return calculateLinearRegressionValue(averagePrice, averageCoefficient, divisible, divisor);
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
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new CFOResult(originalData[currentIndex].getTickTime(),
                    calculateChandeForecastOscillator(linearRegressionValues[currentIndex], originalData[currentIndex].getPriceByType(priceType)));
        }
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
