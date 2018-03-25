package pro.crypto.helper;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

public class MathHelper {

    public static BigDecimal scaleAndRoundValue(BigDecimal value) {
        return isNull(value) ? null : value.setScale(10, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal divide(BigDecimal divisible, BigDecimal divisor) {
        return isNull(divisible) || isNull(divisor) || divisor.compareTo(BigDecimal.ZERO) == 0
                ? null
                : divisible.divide(divisor, 10, BigDecimal.ROUND_HALF_UP);
    }

}
