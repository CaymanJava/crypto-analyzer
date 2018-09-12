package pro.crypto.analyzer.helper;

import pro.crypto.model.Signal;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.NEUTRAL;
import static pro.crypto.model.Signal.SELL;

public class DynamicLineCrossFinder {

    private final BigDecimal[] firstLineValues;
    private final BigDecimal[] secondLineValues;

    public DynamicLineCrossFinder(BigDecimal[] firstLineValues, BigDecimal[] secondLineValues) {
        this.firstLineValues = firstLineValues;
        this.secondLineValues = secondLineValues;
    }

    public Signal[] find() {
        return IntStream.range(0, firstLineValues.length)
                .mapToObj(this::findCrossSignal)
                .toArray(Signal[]::new);
    }

    private Signal findCrossSignal(int currentIndex) {
        return isPossibleDefineCrossSignal(currentIndex)
                ? defineCrossSignal(currentIndex)
                : NEUTRAL;
    }

    private boolean isPossibleDefineCrossSignal(int currentIndex) {
        return currentIndex > 0
                && nonNull(firstLineValues[currentIndex - 1])
                && nonNull(firstLineValues[currentIndex])
                && nonNull(secondLineValues[currentIndex - 1])
                && nonNull(secondLineValues[currentIndex]);
    }

    private Signal defineCrossSignal(int currentIndex) {
        return isLineIntersection(currentIndex)
                ? defineCrossLineSignal(currentIndex)
                : NEUTRAL;
    }

    private boolean isLineIntersection(int currentIndex) {
        return firstLineValues[currentIndex - 1].compareTo(secondLineValues[currentIndex - 1])
                != firstLineValues[currentIndex].compareTo(secondLineValues[currentIndex]);
    }

    private Signal defineCrossLineSignal(int currentIndex) {
        if (isCrossUpLine(currentIndex)) {
            return BUY;
        }
        if (isCrossDownLine(currentIndex)) {
            return SELL;
        }
        return NEUTRAL;
    }

    private boolean isCrossUpLine(int currentIndex) {
        return firstLineValues[currentIndex - 1].compareTo(secondLineValues[currentIndex - 1]) < 0
                && firstLineValues[currentIndex].compareTo(secondLineValues[currentIndex]) >= 0;
    }

    private boolean isCrossDownLine(int currentIndex) {
        return firstLineValues[currentIndex - 1].compareTo(secondLineValues[currentIndex - 1]) > 0
                && firstLineValues[currentIndex].compareTo(secondLineValues[currentIndex]) <= 0;
    }

}
