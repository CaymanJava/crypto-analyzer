package pro.crypto.helper;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class IncreasedQualifier {

    public static Boolean[] define(BigDecimal[] values) {
        return IntStream.range(0, values.length)
                .mapToObj(idx -> defineIncreasedFlag(values, idx))
                .toArray(Boolean[]::new);
    }

    private static Boolean defineIncreasedFlag(BigDecimal[] values, int currentIndex) {
        return currentIndex == 0 || isNull(values[currentIndex]) && isNull(values[currentIndex - 1])
                ? null
                : defineIncreasedFlag(values[currentIndex], values[currentIndex - 1]);
    }

    private static Boolean defineIncreasedFlag(BigDecimal currentValue, BigDecimal previousValue) {
        return !(isNull(previousValue) && nonNull(currentValue)) && currentValue.compareTo(previousValue) >= 0;
    }

}
