package pro.crypto.helper;

import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

public class TrueRangeCalculator {

    public static BigDecimal[] calculate(Tick[] originalData) {
        BigDecimal[] trueRangeValues = new BigDecimal[originalData.length];
        trueRangeValues[0] = calculateFirstTrueRangeValue(originalData[0]);
        for (int currentIndex = 1; currentIndex < trueRangeValues.length; currentIndex++) {
            trueRangeValues[currentIndex] = calculateTrueRange(originalData, currentIndex);
        }
        return trueRangeValues;
    }

    private static BigDecimal calculateFirstTrueRangeValue(Tick firstTick) {
        return MathHelper.scaleAndRound(firstTick.getHigh().subtract(firstTick.getLow()));
    }

    private static BigDecimal calculateTrueRange(Tick[] originalData, int currentIndex) {
        BigDecimal firstRange = originalData[currentIndex].getHigh().subtract(originalData[currentIndex].getLow());
        BigDecimal secondRange = originalData[currentIndex].getHigh().subtract(originalData[currentIndex - 1].getClose());
        BigDecimal thirdRange = originalData[currentIndex].getClose().subtract(originalData[currentIndex].getLow());
        return MathHelper.max(MathHelper.max(firstRange, secondRange), thirdRange);
    }

}
