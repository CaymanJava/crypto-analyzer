package pro.crypto.analyzer.asi;

import pro.crypto.helper.DefaultDivergenceAnalyzer;
import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.asi.ASIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.Strength.STRONG;
import static pro.crypto.model.Strength.WEAK;

public class ASIAnalyzer implements Analyzer<ASIAnalyzerResult> {

    private final Tick[] originalData;
    private final ASIResult[] indicatorResults;

    private BigDecimal[] indicatorValues;
    private ASIAnalyzerResult[] result;

    public ASIAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (ASIResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        SignalStrength[] divergenceSignals = findDivergenceSignals();
        SignalStrength[] signalLineCrossSignals = findSignalLineCrossSignals();
        SignalStrength[] mergedSignals = mergeSignalsStrength(divergenceSignals, signalLineCrossSignals);
        buildASIAnalyzerResult(mergedSignals);
    }

    @Override
    public ASIAnalyzerResult[] getResult() {
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

    private void buildASIAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new ASIAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx]))
                .toArray(ASIAnalyzerResult[]::new);
    }

}
