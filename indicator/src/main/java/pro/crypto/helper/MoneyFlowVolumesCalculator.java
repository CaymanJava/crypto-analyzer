package pro.crypto.helper;

import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static pro.crypto.helper.MathHelper.divide;
import static pro.crypto.helper.MathHelper.scaleAndRound;

public class MoneyFlowVolumesCalculator {

    public static BigDecimal[] calculate(Tick[] data) {
        BigDecimal[] moneyFlowMultipliers = calculateMoneyFlowMultipliers(data);
        return IntStream.range(0, data.length)
                .mapToObj(idx -> scaleAndRound(data[idx].getBaseVolume().multiply(moneyFlowMultipliers[idx])))
                .toArray(BigDecimal[]::new);
    }

    private static BigDecimal[] calculateMoneyFlowMultipliers(Tick[] data) {
        return Stream.of(data)
                .map(MoneyFlowVolumesCalculator::calculateMoneyFlowMultiplier)
                .toArray(BigDecimal[]::new);
    }

    // MFM = ((CLOSE - LOW - HIGH + CLOSE)) / (HIGH - LOW)
    private static BigDecimal calculateMoneyFlowMultiplier(Tick tick) {
        return divide(tick.getClose().subtract(tick.getLow()).subtract(tick.getHigh()).add(tick.getClose()),
                tick.getHigh().subtract(tick.getLow()));
    }

}
