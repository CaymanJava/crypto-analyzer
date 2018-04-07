package pro.crypto.helper;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

public class MathHelper {

    public static BigDecimal scaleAndRound(BigDecimal value) {
        return isNull(value) ? null : value.setScale(10, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal divide(BigDecimal divisible, BigDecimal divisor) {
        return isNull(divisible) || isNull(divisor) || divisor.compareTo(BigDecimal.ZERO) == 0
                ? null
                : divisible.divide(divisor, 10, BigDecimal.ROUND_HALF_UP);
    }

    static BigDecimal max(BigDecimal firstValue, BigDecimal secondValue) {
        if (isNull(firstValue) || isNull(secondValue)) return null;
        return firstValue.compareTo(secondValue) > 0 ? firstValue : secondValue;
    }

    public static BigDecimal min(BigDecimal firstValue, BigDecimal secondValue) {
        if (isNull(firstValue) || isNull(secondValue)) return null;
        return firstValue.compareTo(secondValue) < 0 ? firstValue : secondValue;
    }

    public static BigDecimal sqrt(BigDecimal value) {
        if (isNull(value)) return null;
        return BigDecimal.valueOf(StrictMath.sqrt(value.doubleValue()));
    }

    public static BigDecimal average(BigDecimal[] values) {
        return divide(calculateSum(values), new BigDecimal(values.length));
    }

    public static BigDecimal sum(BigDecimal... values) {
        return calculateSum(values);
    }

    private static BigDecimal calculateSum(BigDecimal[] values) {
        return scaleAndRound(Stream.of(values)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

}
