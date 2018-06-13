package pro.crypto.helper;

import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class PriceDifferencesCalculator {

    public static BigDecimalTuple[] calculateCloseDifference(Tick[] data) {
        BigDecimalTuple[] priceDifferences = new BigDecimalTuple[data.length];
        priceDifferences[0] = new BigDecimalTuple(BigDecimal.ZERO, BigDecimal.ZERO);
        for (int currentIndex = 1; currentIndex < priceDifferences.length; currentIndex++) {
            priceDifferences[currentIndex] = calculateCloseDifference(data, currentIndex);
        }
        return priceDifferences;
    }

    public static BigDecimalTuple[] calculateOpenCloseDifference(Tick[] data) {
        return Stream.of(data)
                .map(PriceDifferencesCalculator::calculateOpenCloseDifference)
                .toArray(BigDecimalTuple[]::new);
    }

    public static BigDecimalTuple[] calculatePriceDifferencesSum(BigDecimalTuple[] priceDifferences, int period) {
        BigDecimalTuple[] priceDifferencesSum = new BigDecimalTuple[priceDifferences.length];
        for (int currentIndex = period - 1; currentIndex < priceDifferences.length; currentIndex++) {
            priceDifferencesSum[currentIndex] = calculatePriceDifferencesSumValue(priceDifferences, currentIndex, period);
        }
        return priceDifferencesSum;
    }

    private static BigDecimalTuple calculateCloseDifference(Tick[] data, int currentIndex) {
        BigDecimal difference =  data[currentIndex].getClose().subtract(data[currentIndex - 1].getClose());
        if (difference.compareTo(BigDecimal.ZERO) > 0) {
            return new BigDecimalTuple(difference, BigDecimal.ZERO);
        }
        return new BigDecimalTuple(BigDecimal.ZERO, difference.abs());
    }

    private static BigDecimalTuple calculateOpenCloseDifference(Tick tick) {
        BigDecimal difference =  tick.getClose().subtract(tick.getOpen());
        if (difference.compareTo(BigDecimal.ZERO) > 0) {
            return new BigDecimalTuple(difference, BigDecimal.ZERO);
        }
        return new BigDecimalTuple(BigDecimal.ZERO, difference.abs());
    }

    private static BigDecimalTuple calculatePriceDifferencesSumValue(BigDecimalTuple[] priceDifferences, int currentIndex, int period) {
        return new BigDecimalTuple(
                calculatePositiveDifferencesSum(priceDifferences, currentIndex, period),
                calculateNegativeDifferencesSum(priceDifferences, currentIndex, period));
    }

    private static BigDecimal calculatePositiveDifferencesSum(BigDecimalTuple[] priceDifferences, int currentIndex, int period) {
        return MathHelper.sum(extractDifferences(priceDifferences, currentIndex, period, BigDecimalTuple::getLeft));
    }

    private static BigDecimal calculateNegativeDifferencesSum(BigDecimalTuple[] priceDifferences, int currentIndex, int period) {
        return MathHelper.sum(extractDifferences(priceDifferences, currentIndex, period, BigDecimalTuple::getRight));
    }

    private static BigDecimal[] extractDifferences(BigDecimalTuple[] priceDifferences, int currentIndex, int period, Function<BigDecimalTuple, BigDecimal> getDifference) {
        return Stream.of(Arrays.copyOfRange(priceDifferences, currentIndex - period + 1, currentIndex + 1))
                .map(getDifference)
                .toArray(BigDecimal[]::new);
    }

}
