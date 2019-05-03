package pro.crypto.helper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class MathHelper {

    public static BigDecimal scaleAndRound(BigDecimal value) {
        return nonNull(value)
                ? value.setScale(10, BigDecimal.ROUND_HALF_UP)
                : null;
    }

    public static BigDecimal divide(BigDecimal divisible, BigDecimal divisor) {
        if (nonNull(divisor) && divisor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return nonNull(divisible) && nonNull(divisor)
                ? divisible.divide(divisor, 20, BigDecimal.ROUND_HALF_UP)
                : null;
    }

    public static BigDecimal min(BigDecimal... values) {
        return scaleAndRound(Stream.of(values)
                .min(BigDecimal::compareTo)
                .orElse(null));
    }

    public static BigDecimal sqrt(BigDecimal value) {
        return nonNull(value)
                ? BigDecimal.valueOf(StrictMath.sqrt(value.doubleValue()))
                : null;
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
        return nonNull(value)
                ? MathHelper.scaleAndRound(new BigDecimal(value))
                : null;
    }

    // https://stackoverflow.com/questions/739532/logarithm-of-a-bigdecimal/745984#745984
    public static BigDecimal log(BigDecimal value, int scale) {
        final int numOfDigits = scale + 2;
        MathContext mc = new MathContext(numOfDigits, RoundingMode.HALF_EVEN);

        if (value.signum() <= 0) {
            throw new ArithmeticException("Log of a negative number! (or zero)");
        }
        if (value.compareTo(BigDecimal.ONE) == 0) {
            return BigDecimal.ZERO;
        }
        if (value.compareTo(BigDecimal.ONE) < 0) {
            return (log((BigDecimal.ONE).divide(value, mc), scale)).negate();
        }

        StringBuilder sb = new StringBuilder();
        int leftDigits = value.precision() - value.scale();
        sb.append(leftDigits - 1).append(".");
        int n = 0;
        while (n < numOfDigits) {
            value = (value.movePointLeft(leftDigits - 1)).pow(10, mc);
            leftDigits = value.precision() - value.scale();
            sb.append(leftDigits - 1);
            n++;
        }
        BigDecimal ans = new BigDecimal(sb.toString());
        ans = ans.round(new MathContext(ans.precision() - ans.scale() + scale, RoundingMode.HALF_EVEN));
        return ans;
    }

    // http://qaru.site/questions/25608/logarithm-of-a-bigdecimal/186892#186892
    public static BigDecimal ln(BigDecimal value) {
        if (isNull(value)) {
            return null;
        }
        MathContext context = new MathContext(15);
        if (value.compareTo(BigDecimal.ONE) == 0) {
            return BigDecimal.ZERO;
        }
        value = value.subtract(BigDecimal.ONE);
        BigDecimal ret = new BigDecimal(31);
        for (long i = 30; i >= 0; i--) {
            BigDecimal n = new BigDecimal(i / 2 + 1).pow(2);
            n = n.multiply(value, context);
            ret = n.divide(ret, context);
            n = new BigDecimal(i + 1);
            ret = ret.add(n, context);
        }
        return value.divide(ret, context);
    }

    private static BigDecimal calculateSum(BigDecimal[] values) {
        return scaleAndRound(Stream.of(values)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

}
