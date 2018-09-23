package pro.crypto.analyzer.co;

import pro.crypto.analyzer.helper.DefaultDivergenceAnalyzer;
import pro.crypto.analyzer.helper.StaticLineCrossFinder;
import pro.crypto.helper.IncreasedQualifier;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.co.COResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.Strength;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Strength.*;

public class COAnalyzer implements Analyzer<COAnalyzerResult> {

    private final static BigDecimal ZERO_LEVEL = ZERO;

    private final Tick[] originalData;
    private final COResult[] indicatorResults;

    private COAnalyzerResult[] result;
    private Boolean[] indicatorIncreases;

    public COAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (COResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        SignalStrength[] divergenceSignals = findDivergenceSignals();
        SignalStrength[] crossSignals = findCrossSignals();
        SignalStrength[] increaseDecreaseSignals = findIncreaseDecreaseSignals();
        SignalStrength[] mergedSignals = mergeSignalsStrength(divergenceSignals, crossSignals, increaseDecreaseSignals);
        buildCOAnalyzerResult(mergedSignals);
    }

    @Override
    public COAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private SignalStrength[] findDivergenceSignals() {
        return Stream.of(new DefaultDivergenceAnalyzer().analyze(originalData, IndicatorResultExtractor.extract(indicatorResults)))
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findCrossSignals() {
        return Stream.of(new StaticLineCrossFinder(IndicatorResultExtractor.extract(indicatorResults), ZERO_LEVEL).find())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findIncreaseDecreaseSignals() {
        indicatorIncreases = calculateIndicatorIncreases();
        return IntStream.range(0, indicatorIncreases.length)
                .mapToObj(this::findIncreaseDecreaseSignal)
                .toArray(SignalStrength[]::new);
    }

    private Boolean[] calculateIndicatorIncreases() {
        return IncreasedQualifier.define(IndicatorResultExtractor.extract(indicatorResults));
    }

    private SignalStrength findIncreaseDecreaseSignal(int currentIndex) {
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

    private SignalStrength defineIncreaseDecreaseSignal(int currentIndex) {
        if (isBuySignal(currentIndex)) {
            return new SignalStrength(BUY, defineBuyStrength(currentIndex));
        }

        if (isSellSignal(currentIndex)) {
            return new SignalStrength(SELL, defineSellStrength(currentIndex));
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

    private Strength defineBuyStrength(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO_LEVEL) > 0 ? NORMAL : STRONG;
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

    private Strength defineSellStrength(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO_LEVEL) > 0 ? STRONG : NORMAL;
    }

    private void buildCOAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new COAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx]))
                .toArray(COAnalyzerResult[]::new);
    }

}
