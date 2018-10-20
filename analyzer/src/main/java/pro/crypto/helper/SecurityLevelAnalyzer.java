package pro.crypto.helper;

import pro.crypto.model.SecurityLevel;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;

public class SecurityLevelAnalyzer {

    private final BigDecimal[] indicatorValues;
    private final BigDecimal overboughtLevel;
    private final BigDecimal oversoldLevel;

    public SecurityLevelAnalyzer(BigDecimal[] indicatorValues, BigDecimal overboughtLevel, BigDecimal oversoldLevel) {
        this.indicatorValues = indicatorValues;
        this.overboughtLevel = overboughtLevel;
        this.oversoldLevel = oversoldLevel;
    }

    public SecurityLevel[] analyze() {
        return IntStream.range(0, indicatorValues.length)
                .mapToObj(this::defineSecurityLevel)
                .toArray(SecurityLevel[]::new);
    }

    private SecurityLevel defineSecurityLevel(int currentIndex) {
        return isPossibleDefineSecurityLevel(currentIndex)
                ? defineSecurityLevel(indicatorValues[currentIndex])
                : SecurityLevel.UNDEFINED;
    }

    private boolean isPossibleDefineSecurityLevel(int currentIndex) {
        return nonNull(indicatorValues[currentIndex]);
    }

    private SecurityLevel defineSecurityLevel(BigDecimal indicatorValue) {
        if (indicatorValue.compareTo(overboughtLevel) >= 0) {
            return SecurityLevel.OVERBOUGHT;
        }

        if (indicatorValue.compareTo(oversoldLevel) <= 0) {
            return SecurityLevel.OVERSOLD;
        }

        return SecurityLevel.NORMAL;
    }

}
