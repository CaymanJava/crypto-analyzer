package pro.crypto.analyzer.di;

import pro.crypto.analyzer.helper.DefaultDivergenceAnalyzer;
import pro.crypto.analyzer.helper.StaticLineCrossFinder;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.di.DIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.tick.Tick;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static pro.crypto.model.Strength.STRONG;
import static pro.crypto.model.Strength.WEAK;

public class DIAnalyzer implements Analyzer<DIAnalyzerResult> {

    private final Tick[] originalData;
    private final DIResult[] indicatorResults;

    private DIAnalyzerResult[] result;

    public DIAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (DIResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        SignalStrength[] divergenceSignals = findDivergenceSignals();
        SignalStrength[] crossSignals = findCrossSignals();
        SignalStrength[] mergedSignals = mergeSignalsStrength(divergenceSignals, crossSignals);
        buildDIAnalyzerResult(mergedSignals);
    }

    @Override
    public DIAnalyzerResult[] getResult() {
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
        return Stream.of(new StaticLineCrossFinder(IndicatorResultExtractor.extract(indicatorResults), ZERO).find())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private void buildDIAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new DIAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx]))
                .toArray(DIAnalyzerResult[]::new);
    }

}
