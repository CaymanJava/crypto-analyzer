package pro.crypto.helper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

    public static BigDecimal min(BigDecimal... values) {
        return scaleAndRound(Stream.of(values)
                .min(BigDecimal::compareTo)
                .orElse(null));
    }

    public static BigDecimal sqrt(BigDecimal value) {
        if (isNull(value)) return null;
        return BigDecimal.valueOf(StrictMath.sqrt(value.doubleValue()));
    }

    public static BigDecimal average(BigDecimal... values) {
        return divide(calculateSum(values), new BigDecimal(values.length));
    }

    public static BigDecimal sum(BigDecimal... values) {
        return calculateSum(values);
    }

    public static BigDecimal max(BigDecimal... values) {
        return scaleAndRound(Stream.of(values)
                .max(BigDecimal::compareTo)
                .orElse(null));
    }

    public static BigDecimal toBigDecimal(Double value) {
        return isNull(value) ? null : MathHelper.scaleAndRound(new BigDecimal(value));
    }

    // https://stackoverflow.com/questions/739532/logarithm-of-a-bigdecimal/745984#745984
    public static BigDecimal log(BigDecimal value, int scale) {
        final int numOfDigits = scale + 2;
        MathContext mc = new MathContext(numOfDigits, RoundingMode.HALF_EVEN);

        if(value.signum() <= 0) {
            throw new ArithmeticException("log of a negative number! (or zero)");
        } else if(value.compareTo(BigDecimal.ONE) == 0) {
            return BigDecimal.ZERO;
        } else if(value.compareTo(BigDecimal.ONE) < 0) {
            return (log((BigDecimal.ONE).divide(value, mc), scale)).negate();
        }

        StringBuilder sb = new StringBuilder();
        int leftDigits = value.precision() - value.scale();
        sb.append(leftDigits - 1).append(".");
        int n = 0;
        while(n < numOfDigits) {
            value = (value.movePointLeft(leftDigits - 1)).pow(10, mc);
            leftDigits = value.precision() - value.scale();
            sb.append(leftDigits - 1);
            n++;
        }
        BigDecimal ans = new BigDecimal(sb.toString());
        ans = ans.round(new MathContext(ans.precision() - ans.scale() + scale, RoundingMode.HALF_EVEN));
        return ans;
    }

    private static BigDecimal calculateSum(BigDecimal[] values) {
        return scaleAndRound(Stream.of(values)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

}
