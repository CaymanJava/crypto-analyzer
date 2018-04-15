package pro.crypto.helper;

import java.math.BigDecimal;
import java.util.Arrays;

public class MinMaxCalculator {

    public static BigDecimal[] calculateMinimumValues(BigDecimal[] data, int period) {
        BigDecimal[] minValues = new BigDecimal[data.length];
        for (int currentIndex = period - 1; currentIndex < minValues.length; currentIndex++) {
            minValues[currentIndex] = MathHelper.min(extractLowValuesForComparing(data, period, currentIndex));
        }
        return minValues;
    }

    public static BigDecimal[] calculateMaximumValues(BigDecimal[] data, int period) {
        BigDecimal[] maxValues = new BigDecimal[data.length];
        for (int currentIndex = period - 1; currentIndex < maxValues.length; currentIndex++) {
            maxValues[currentIndex] = MathHelper.max(extractHighValuesForComparing(data, period, currentIndex));
        }
        return maxValues;
    }

    private static BigDecimal[] extractLowValuesForComparing(BigDecimal[] data, int period, int currentIndex) {
        return Arrays.copyOfRange(data, currentIndex - period + 1, currentIndex + 1);
    }

    private static BigDecimal[] extractHighValuesForComparing(BigDecimal[] data, int period, int currentIndex) {
        return Arrays.copyOfRange(data, currentIndex - period + 1, currentIndex + 1);
    }

}
