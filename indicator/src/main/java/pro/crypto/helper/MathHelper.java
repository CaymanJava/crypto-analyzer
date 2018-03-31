package pro.crypto.helper;

import java.math.BigDecimal;

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

    public static BigDecimal max(BigDecimal firstValue, BigDecimal secondValue) {
        if (isNull(firstValue) || isNull(secondValue)) return null;
        return firstValue.compareTo(secondValue) > 0 ? firstValue : secondValue;
    }

    public static BigDecimal min(BigDecimal firstValue, BigDecimal secondValue) {
        if (isNull(firstValue) || isNull(secondValue)) return null;
        return firstValue.compareTo(secondValue) < 0 ? firstValue : secondValue;
    }

}
