package pro.crypto.helper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

public class MinMaxFinder {

    public static BigDecimal[] findMinValues(BigDecimal[] data, int period) {
        return findValues(data, period, MathHelper::min);
    }

    public static BigDecimal[] findMaxValues(BigDecimal[] data, int period) {
        return findValues(data, period, MathHelper::max);
    }

    public static BigDecimal[] findMinExcludingLast(BigDecimal[] data, int period) {
        return findValuesExcludingLast(data, period, MathHelper::min);
    }

    public static BigDecimal[] findMaxExcludingLast(BigDecimal[] data, int period) {
        return findValuesExcludingLast(data, period, MathHelper::max);
    }

    private static BigDecimal[] findValues(BigDecimal[] data, int period, Function<BigDecimal[], BigDecimal> mathFunction) {
        final BigDecimal[] values = new BigDecimal[data.length];
        IntStream.range(period - 1, data.length)
                .forEach(idx -> values[idx] = mathFunction.apply(extractValues(data, period, idx)));
        return values;
    }

    private static BigDecimal[] findValuesExcludingLast(BigDecimal[] data, int period, Function<BigDecimal[], BigDecimal> mathFunction) {
        final BigDecimal[] values = new BigDecimal[data.length];
        IntStream.range(period, data.length)
                .forEach(idx -> values[idx] = mathFunction.apply(extractValues(data, period, idx - 1)));
        return values;
    }

    private static BigDecimal[] extractValues(BigDecimal[] data, int period, int currentIndex) {
        return Arrays.copyOfRange(data, currentIndex - period + 1, currentIndex + 1);
    }

}
