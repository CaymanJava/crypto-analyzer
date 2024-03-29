package pro.crypto.analyzer.tmf;

import pro.crypto.helper.DefaultDivergenceAnalyzer;
import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.tmf.TMFResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static pro.crypto.helper.IndicatorResultExtractor.extractSignalLineValues;
import static pro.crypto.model.analyzer.Strength.NORMAL;
import static pro.crypto.model.analyzer.Strength.STRONG;
import static pro.crypto.model.analyzer.Strength.WEAK;

public class TMFAnalyzer implements Analyzer<TMFAnalyzerResult> {

    private final Tick[] originalData;
    private final TMFResult[] indicatorResults;

    private BigDecimal[] indicatorValues;
    private TMFAnalyzerResult[] result;

    public TMFAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (TMFResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        SignalStrength[] divergenceSignals = findDivergenceSignals();
        SignalStrength[] zeroLineCrossSignals = findZeroLineCrossSignals();
        SignalStrength[] signalLineCrossSignals = findSignalLineCrossSignals();
        SignalStrength[] mergedSignals = SignalArrayMerger.mergeSignalsStrength(divergenceSignals, zeroLineCrossSignals, signalLineCrossSignals);
        buildTMFAnalyzerResult(mergedSignals);
    }

    @Override
    public TMFAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private SignalStrength[] findDivergenceSignals() {
        return Stream.of(new DefaultDivergenceAnalyzer(originalData, indicatorValues).analyze())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findZeroLineCrossSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, ZERO).analyze())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findSignalLineCrossSignals() {
        return Stream.of(new DynamicLineCrossAnalyzer(indicatorValues, extractSignalLineValues(indicatorResults)).analyze())
                .map(signal -> toSignalStrength(signal, NORMAL))
                .toArray(SignalStrength[]::new);
    }

    private void buildTMFAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new TMFAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx]))
                .toArray(TMFAnalyzerResult[]::new);
    }

}
