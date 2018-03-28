package pro.crypto.helper;

import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static pro.crypto.helper.MathHelper.divide;
import static pro.crypto.helper.MathHelper.scaleAndRound;

public class MoneyFlowVolumesCounter {

    public static BigDecimal[] countMoneyFlowVolumes(Tick[] originalData) {
        BigDecimal[] moneyFlowMultipliers = countMoneyFlowMultipliers(originalData);
        BigDecimal[] moneyFlowVolumes = new BigDecimal[originalData.length];
        for (int i = 0; i < originalData.length; i++) {
            moneyFlowVolumes[i] = scaleAndRound(originalData[i].getBaseVolume().multiply(moneyFlowMultipliers[i]));
        }
        return moneyFlowVolumes;
    }

    private static BigDecimal[] countMoneyFlowMultipliers(Tick[] originalData) {
        return Stream.of(originalData)
                .map(MoneyFlowVolumesCounter::countMoneyFlowMultiplier)
                .toArray(BigDecimal[]::new);
    }

    // MFM = ((CLOSE - LOW - HIGH + CLOSE)) / (HIGH - LOW)
    private static BigDecimal countMoneyFlowMultiplier(Tick tick) {
        return divide(tick.getClose().subtract(tick.getLow()).subtract(tick.getHigh()).add(tick.getClose()),
                tick.getHigh().subtract(tick.getLow()));
    }

}
