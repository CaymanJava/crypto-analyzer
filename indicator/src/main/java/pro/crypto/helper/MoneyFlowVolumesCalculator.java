package pro.crypto.helper;

import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static pro.crypto.helper.MathHelper.divide;
import static pro.crypto.helper.MathHelper.scaleAndRound;

public class MoneyFlowVolumesCalculator {

    public static BigDecimal[] calculate(Tick[] originalData) {
        BigDecimal[] moneyFlowMultipliers = calculateMoneyFlowMultipliers(originalData);
        BigDecimal[] moneyFlowVolumes = new BigDecimal[originalData.length];
        for (int i = 0; i < originalData.length; i++) {
            moneyFlowVolumes[i] = scaleAndRound(originalData[i].getBaseVolume().multiply(moneyFlowMultipliers[i]));
        }
        return moneyFlowVolumes;
    }

    private static BigDecimal[] calculateMoneyFlowMultipliers(Tick[] originalData) {
        return Stream.of(originalData)
                .map(MoneyFlowVolumesCalculator::calculateMoneyFlowMultiplier)
                .toArray(BigDecimal[]::new);
    }

    // MFM = ((CLOSE - LOW - HIGH + CLOSE)) / (HIGH - LOW)
    private static BigDecimal calculateMoneyFlowMultiplier(Tick tick) {
        return divide(tick.getClose().subtract(tick.getLow()).subtract(tick.getHigh()).add(tick.getClose()),
                tick.getHigh().subtract(tick.getLow()));
    }

}
