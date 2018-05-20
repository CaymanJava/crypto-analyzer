package pro.crypto.indicators.ma;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.MAResultExtractor;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.DOUBLE_EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;

public class DoubleExponentialMovingAverage extends MovingAverage {

    private final Tick[] originalData;
    private final int period;
    private final BigDecimal alphaCoefficient;

    DoubleExponentialMovingAverage(Tick[] originalData, int period, PriceType priceType, BigDecimal alphaCoefficient) {
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
        this.alphaCoefficient = alphaCoefficient;
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return DOUBLE_EXPONENTIAL_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        result = new MAResult[originalData.length];
        BigDecimal[] exponentialMovingAverage = calculateExponentialMovingAverageValues(originalData, priceType);
        BigDecimal[] doubleSmoothedEMA = calculateDoubleSmoothedEMA(exponentialMovingAverage);
        calculateDoubleExponentialMovingAverage(exponentialMovingAverage, doubleSmoothedEMA);
    }

    private void checkIncomingData() {
        checkIncomingData(originalData, period, priceType);
        checkOriginalDataSize(originalData, period + period);
    }

    private BigDecimal[] calculateDoubleSmoothedEMA(BigDecimal[] exponentialMovingAverage) {
        BigDecimal[] doubleSmoothedEMA = calculateExponentialMovingAverageValues(FakeTicksCreator.createWithCloseOnly(exponentialMovingAverage), PriceType.CLOSE);
        BigDecimal[] result = new BigDecimal[exponentialMovingAverage.length];
        System.arraycopy(doubleSmoothedEMA, 0, result, period - 1, doubleSmoothedEMA.length);
        return result;
    }

    private BigDecimal[] calculateExponentialMovingAverageValues(Tick[] data, PriceType priceType) {
        return MAResultExtractor.extract(calculateExponentialMovingAverage(data, priceType));
    }

    private MAResult[] calculateExponentialMovingAverage(Tick[] data, PriceType priceType) {
        return MovingAverageFactory.create(buildMARequest(data, priceType))
                .getResult();
    }

    private MARequest buildMARequest(Tick[] data, PriceType priceType) {
        return MARequest.builder()
                .originalData(data)
                .priceType(priceType)
                .alphaCoefficient(alphaCoefficient)
                .period(period)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

    private void calculateDoubleExponentialMovingAverage(BigDecimal[] exponentialMovingAverage, BigDecimal[] doubleSmoothedEMA) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new MAResult(
                    originalData[currentIndex].getTickTime(),
                    calculateDoubleExponentialMovingAverage(exponentialMovingAverage[currentIndex],
                            doubleSmoothedEMA[currentIndex])
            );
        }
    }

    private BigDecimal calculateDoubleExponentialMovingAverage(BigDecimal exponentialMovingAverageValue, BigDecimal doubleSmoothedEMAValue) {
        return nonNull(exponentialMovingAverageValue) && nonNull(doubleSmoothedEMAValue)
                ? calculateDoubleExponentialMovingAverageValue(exponentialMovingAverageValue, doubleSmoothedEMAValue)
                : null;
    }

    private BigDecimal calculateDoubleExponentialMovingAverageValue(BigDecimal exponentialMovingAverageValue, BigDecimal doubleSmoothedEMAValue) {
        return exponentialMovingAverageValue.multiply(new BigDecimal(2)).subtract(doubleSmoothedEMAValue);
    }

}
