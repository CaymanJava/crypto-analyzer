package pro.crypto.helper;

import pro.crypto.helper.model.BigDecimalTuple;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PriceDifferencesCalculator {

    public static BigDecimalTuple[] calculateCloseDifference(Tick[] data) {
        return IntStream.range(0, data.length)
                .mapToObj(idx -> calculateCloseDifference(data, idx))
                .toArray(BigDecimalTuple[]::new);
    }

    public static BigDecimalTuple[] calculateOpenCloseDifference(Tick[] data) {
        return Stream.of(data)
                .map(PriceDifferencesCalculator::calculateOpenCloseDifference)
                .toArray(BigDecimalTuple[]::new);
    }

    public static BigDecimalTuple[] calculatePriceDifferencesSum(BigDecimalTuple[] priceDifferences, int period) {
        final BigDecimalTuple[] priceDifferencesSum = new BigDecimalTuple[priceDifferences.length];
        IntStream.range(period - 1, priceDifferences.length)
                .forEach(idx -> priceDifferencesSum[idx] = calculatePriceDifferencesSumValue(priceDifferences, idx, period));
        return priceDifferencesSum;
    }

    private static BigDecimalTuple calculateCloseDifference(Tick[] data, int currentIndex) {
        if (currentIndex == 0) {
            return new BigDecimalTuple(BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return buildDifference(data[currentIndex].getClose().subtract(data[currentIndex - 1].getClose()));
    }

    private static BigDecimalTuple calculateOpenCloseDifference(Tick tick) {
        return buildDifference(tick.getClose().subtract(tick.getOpen()));
    }

    private static BigDecimalTuple buildDifference(BigDecimal difference) {
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

    private static BigDecimal[] extractDifferences(BigDecimalTuple[] priceDifferences, int currentIndex, int period,
                                                   Function<BigDecimalTuple, BigDecimal> getDifference) {
        return Stream.of(Arrays.copyOfRange(priceDifferences, currentIndex - period + 1, currentIndex + 1))
                .map(getDifference)
                .toArray(BigDecimal[]::new);
    }

}
