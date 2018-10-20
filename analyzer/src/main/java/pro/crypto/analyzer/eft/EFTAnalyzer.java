package pro.crypto.analyzer.eft;

import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.eft.EFTResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.SignalStrength;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static pro.crypto.model.Strength.NORMAL;
import static pro.crypto.model.Strength.STRONG;

public class EFTAnalyzer implements Analyzer<EFTAnalyzerResult> {

    private final EFTResult[] indicatorResults;

    private EFTAnalyzerResult[] result;

    public EFTAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (EFTResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        BigDecimal[] indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
        SignalStrength[] zeroLineCrossSignals = findZeroLineCrossSignals(indicatorValues);
        SignalStrength[] triggerCrossSignals = findTriggerCrossSignals(indicatorValues);
        SignalStrength[] mergedSignals = mergeSignalsStrength(zeroLineCrossSignals, triggerCrossSignals);
        buildEFTAnalyzerResult(mergedSignals);
    }

    @Override
    public EFTAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private SignalStrength[] findZeroLineCrossSignals(BigDecimal[] indicatorValues) {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, ZERO).analyze())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findTriggerCrossSignals(BigDecimal[] indicatorValues) {
        return Stream.of(new DynamicLineCrossAnalyzer(indicatorValues, extractTriggerValues()).analyze())
                .map(signal -> toSignalStrength(signal, NORMAL))
                .toArray(SignalStrength[]::new);
    }

    private BigDecimal[] extractTriggerValues() {
        return Stream.of(indicatorResults)
                .map(EFTResult::getTrigger)
                .toArray(BigDecimal[]::new);
    }

    private void buildEFTAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new EFTAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx]))
                .toArray(EFTAnalyzerResult[]::new);
    }

}
