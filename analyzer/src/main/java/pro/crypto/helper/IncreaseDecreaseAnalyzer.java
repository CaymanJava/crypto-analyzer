package pro.crypto.helper;

import pro.crypto.model.Signal;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;

public class IncreaseDecreaseAnalyzer {

    private final BigDecimal[] indicatorValues;

    private Boolean[] indicatorIncreases;

    public IncreaseDecreaseAnalyzer(BigDecimal[] indicatorValues) {
        this.indicatorValues = indicatorValues;
    }

    public Signal[] analyze() {
        indicatorIncreases = calculateIndicatorIncreases();
        return IntStream.range(0, indicatorValues.length)
                .mapToObj(this::findIncreaseDecreaseSignal)
                .toArray(Signal[]::new);
    }

    private Boolean[] calculateIndicatorIncreases() {
        return IncreasedQualifier.define(indicatorValues);
    }

    private Signal findIncreaseDecreaseSignal(int currentIndex) {
        return isPossibleDefineIncreaseDecreaseSignal(currentIndex)
                ? defineIncreaseDecreaseSignal(currentIndex)
                : null;
    }

    private boolean isPossibleDefineIncreaseDecreaseSignal(int currentIndex) {
        return currentIndex > 2
                && nonNull(indicatorIncreases[currentIndex - 2])
                && nonNull(indicatorIncreases[currentIndex - 1])
                && nonNull(indicatorIncreases[currentIndex]);
    }

    private Signal defineIncreaseDecreaseSignal(int currentIndex) {
        if (isBuySignal(currentIndex)) {
            return BUY;
        }

        if (isSellSignal(currentIndex)) {
            return SELL;
        }

        return null;
    }

    private boolean isBuySignal(int currentIndex) {
        return lastIndicatorValuesDecrease(currentIndex) && isCurrentIndicatorValueIncrease(currentIndex);
    }

    private boolean lastIndicatorValuesDecrease(int currentIndex) {
        return !indicatorIncreases[currentIndex - 2] && !indicatorIncreases[currentIndex - 1];
    }

    private boolean isCurrentIndicatorValueIncrease(int currentIndex) {
        return indicatorIncreases[currentIndex];
    }

    private boolean isSellSignal(int currentIndex) {
        return lastIndicatorValuesIncrease(currentIndex) && isCurrentIndicatorValueDecrease(currentIndex);
    }

    private boolean lastIndicatorValuesIncrease(int currentIndex) {
        return indicatorIncreases[currentIndex - 2] && indicatorIncreases[currentIndex - 1];
    }

    private boolean isCurrentIndicatorValueDecrease(int currentIndex) {
        return !indicatorIncreases[currentIndex];
    }

}
