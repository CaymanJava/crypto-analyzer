package pro.crypto.helper;

import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

public class TrueRangeCounter {

    public static BigDecimal[] countTrueRangeValues(Tick[] originalData) {
        BigDecimal[] trueRangeValues = new BigDecimal[originalData.length];
        trueRangeValues[0] = countFirstTrueRangeValue(originalData[0]);
        for (int currentIndex = 1; currentIndex < trueRangeValues.length; currentIndex++) {
            trueRangeValues[currentIndex] = countTrueRange(originalData, currentIndex);
        }
        return trueRangeValues;
    }

    private static BigDecimal countFirstTrueRangeValue(Tick firstTick) {
        return MathHelper.scaleAndRound(firstTick.getHigh().subtract(firstTick.getLow()));
    }

    private static BigDecimal countTrueRange(Tick[] originalData, int currentIndex) {
        BigDecimal firstRange = originalData[currentIndex].getHigh().subtract(originalData[currentIndex].getLow());
        BigDecimal secondRange = originalData[currentIndex].getHigh().subtract(originalData[currentIndex - 1].getClose());
        BigDecimal thirdRange = originalData[currentIndex].getClose().subtract(originalData[currentIndex].getLow());
        return MathHelper.max(MathHelper.max(firstRange, secondRange), thirdRange);
    }

}
