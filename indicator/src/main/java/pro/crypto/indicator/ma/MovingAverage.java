package pro.crypto.indicator.ma;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

public abstract class MovingAverage implements Indicator<MAResult> {

    protected MAResult[] result;
    protected PriceType priceType;

    @Override
    public MAResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    protected void checkIncomingData(Tick[] originalData, int period, PriceType priceType) {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPriceType(priceType);
        checkPeriod(period);
    }

    void calculateSimpleAverage(int fromIndex, int currentIndex, Tick[] originalData) {
        BigDecimal simpleAverage = MathHelper.average(extractPricesBetweenIndexes(fromIndex, currentIndex, originalData));
        result[currentIndex] = buildMovingAverageResult(currentIndex, originalData, simpleAverage);
    }

    void fillInInitialPositions(Tick[] originalData, int period) {
        IntStream.range(0, period - 1)
                .forEach(idx -> result[idx] = new MAResult(
                        originalData[idx].getTickTime(),
                        null));
    }

    void initResultArray(int length) {
        result = new MAResult[length];
    }

    BigDecimal calculateAndGetSimpleAverage(int fromIndex, int toIndex, Tick[] originalData) {
        return MathHelper.average(extractPricesBetweenIndexes(fromIndex, toIndex, originalData));
    }

    // EMAt = α * Pt + (1 - α) * EMAt-1
    BigDecimal calculateExponentialAverage(Tick[] originalData, int currentIndex, BigDecimal alphaCoefficient) {
        return MathHelper.sum(
                alphaCoefficient.multiply(originalData[currentIndex].getPriceByType(priceType)),
                new BigDecimal(1).subtract(alphaCoefficient).multiply(result[currentIndex - 1].getIndicatorValue()));
    }

    private BigDecimal[] extractPricesBetweenIndexes(int fromIndex, int toIndex, Tick[] originalData) {
        return Stream.of(Arrays.copyOfRange(originalData, fromIndex, toIndex + 1))
                .map(tick -> tick.getPriceByType(priceType))
                .toArray(BigDecimal[]::new);
    }

    private MAResult buildMovingAverageResult(int currentIndex, Tick[] originalData, BigDecimal simpleAverage) {
        return new MAResult(
                originalData[currentIndex].getTickTime(),
                simpleAverage);
    }

}
