package pro.crypto.analyzer.co;

import pro.crypto.helper.DefaultDivergenceAnalyzer;
import pro.crypto.helper.IncreaseDecreaseAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.co.COResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Strength.*;

public class COAnalyzer implements Analyzer<COAnalyzerResult> {

    private final Tick[] originalData;
    private final COResult[] indicatorResults;

    private COAnalyzerResult[] result;
    private BigDecimal[] indicatorValues;

    public COAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (COResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
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

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private SignalStrength[] findDivergenceSignals() {
        return Stream.of(new DefaultDivergenceAnalyzer().analyze(originalData, indicatorValues))
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findCrossSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, ZERO).analyze())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findIncreaseDecreaseSignals() {
        Signal[] signals = new IncreaseDecreaseAnalyzer(indicatorValues).analyze();
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> toSignalStrength(signals[idx], defineStrength(signals[idx], idx)))
                .toArray(SignalStrength[]::new);
    }

    private Strength defineStrength(Signal signal, int currentIndex) {
        if (signal == BUY) {
            return defineBuyStrength(currentIndex);
        }

        if (signal == SELL) {
            return defineSellStrength(currentIndex);
        }

        return null;
    }

    private Strength defineBuyStrength(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO) > 0 ? NORMAL : STRONG;
    }

    private Strength defineSellStrength(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO) > 0 ? STRONG : NORMAL;
    }

    private void buildCOAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new COAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx]))
                .toArray(COAnalyzerResult[]::new);
    }

}
