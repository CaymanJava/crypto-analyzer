package pro.crypto.helper;

import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

public class TrueRangeCalculator {

    public static BigDecimal[] calculate(Tick[] data) {
        return IntStream.range(0, data.length)
                .mapToObj(idx -> calculateTrueRange(data, idx))
                .toArray(BigDecimal[]::new);
    }

    private static BigDecimal calculateTrueRange(Tick[] data, int currentIndex) {
        return currentIndex == 0
                ? calculateFirstTrueRangeValue(data[0])
                : calculateTrueRange(data[currentIndex], data[currentIndex - 1]);
    }

    private static BigDecimal calculateFirstTrueRangeValue(Tick firstTick) {
        return MathHelper.scaleAndRound(firstTick.getHigh().subtract(firstTick.getLow()));
    }

    private static BigDecimal calculateTrueRange(Tick currentTick, Tick previousTick) {
        BigDecimal firstRange = currentTick.getHigh().subtract(currentTick.getLow());
        BigDecimal secondRange = currentTick.getHigh().subtract(previousTick.getClose());
        BigDecimal thirdRange = currentTick.getClose().subtract(currentTick.getLow());
        return MathHelper.max(firstRange, secondRange, thirdRange);
    }


}
