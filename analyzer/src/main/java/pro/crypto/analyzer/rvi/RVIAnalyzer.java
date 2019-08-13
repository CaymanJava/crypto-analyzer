package pro.crypto.analyzer.rvi;

import pro.crypto.helper.DefaultDivergenceAnalyzer;
import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.indicator.rvi.RVIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.analyzer.Strength.STRONG;
import static pro.crypto.model.analyzer.Strength.WEAK;

public class RVIAnalyzer implements Analyzer<RVIAnalyzerResult> {

    private final Tick[] originalData;
    private final RVIResult[] indicatorResults;

    private BigDecimal[] indicatorValues;
    private RVIAnalyzerResult[] result;

    public RVIAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (RVIResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        SignalStrength[] divergenceSignals = findDivergenceSignals();
        SignalStrength[] signalLineCrossSignals = findSignalLineCrossSignals();
        SignalStrength[] mergedSignals = SignalArrayMerger.mergeSignalsStrength(divergenceSignals, signalLineCrossSignals);
        buildRVIAnalyzerResult(mergedSignals);
    }

    @Override
    public RVIAnalyzerResult[] getResult() {
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

    private SignalStrength[] findSignalLineCrossSignals() {
        return Stream.of(new DynamicLineCrossAnalyzer(indicatorValues,
                IndicatorResultExtractor.extractSignalLineValues(indicatorResults)).analyze())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private void buildRVIAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new RVIAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx]))
                .toArray(RVIAnalyzerResult[]::new);
    }

}
