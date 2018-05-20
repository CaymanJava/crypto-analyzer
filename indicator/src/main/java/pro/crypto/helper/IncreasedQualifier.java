package pro.crypto.helper;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class IncreasedQualifier {

    public static Boolean[] define(BigDecimal[] values) {
        Boolean[] increasedFlags = new Boolean[values.length];
        for (int currentIndex = 1; currentIndex < increasedFlags.length; currentIndex++) {
            increasedFlags[currentIndex] = defineIncreasedFlag(values, currentIndex);
        }
        return increasedFlags;
    }

    private static Boolean defineIncreasedFlag(BigDecimal[] awesomeOscillatorValues, int currentIndex) {
        return isNull(awesomeOscillatorValues[currentIndex]) && isNull(awesomeOscillatorValues[currentIndex - 1])
                ? null
                : defineIncreasedFlag(awesomeOscillatorValues[currentIndex], awesomeOscillatorValues[currentIndex - 1]);
    }

    private static Boolean defineIncreasedFlag(BigDecimal currentValue, BigDecimal previousValue) {
        return !(isNull(previousValue) && nonNull(currentValue)) && currentValue.compareTo(previousValue) >= 0;
    }

}
