package pro.crypto.helper;

import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

public class PriceDifferencesCalculator {

    public static BigDecimalTuple[] calculate(Tick[] data) {
        BigDecimalTuple[] priceDifferences = new BigDecimalTuple[data.length];
        priceDifferences[0] = new BigDecimalTuple(BigDecimal.ZERO, BigDecimal.ZERO);
        for (int currentIndex = 1; currentIndex < priceDifferences.length; currentIndex++) {
            priceDifferences[currentIndex] = calculateDifference(data, currentIndex);
        }
        return priceDifferences;
    }

    private static BigDecimalTuple calculateDifference(Tick[] data, int currentIndex) {
        BigDecimal difference =  data[currentIndex].getClose().subtract(data[currentIndex - 1].getClose());
        if (difference.compareTo(BigDecimal.ZERO) > 0) {
            return new BigDecimalTuple(difference, BigDecimal.ZERO);
        }
        return new BigDecimalTuple(BigDecimal.ZERO, difference.abs());
    }

}
