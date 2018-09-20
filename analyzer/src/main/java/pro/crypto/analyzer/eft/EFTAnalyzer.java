package pro.crypto.analyzer.eft;

import pro.crypto.analyzer.helper.DynamicLineCrossFinder;
import pro.crypto.analyzer.helper.SignalStrengthMerger;
import pro.crypto.analyzer.helper.StaticLineCrossFinder;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.eft.EFTResult;
import pro.crypto.model.*;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.NEUTRAL;
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
        BigDecimal[] indicatorValues = IndicatorResultExtractor.extract(indicatorResults);
        SignalStrength[] zeroLineCrossSignals = findZeroLineCrossSignals(indicatorValues);
        SignalStrength[] triggerCrossSignals = findTriggerCrossSignals(indicatorValues);
        SignalStrength[] mergedSignals = mergeSignals(zeroLineCrossSignals, triggerCrossSignals);
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
        return Stream.of(new StaticLineCrossFinder(indicatorValues, ZERO).find())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findTriggerCrossSignals(BigDecimal[] indicatorValues) {
        return Stream.of(new DynamicLineCrossFinder(indicatorValues, extractTriggerValues()).find())
                .map(signal -> toSignalStrength(signal, NORMAL))
                .toArray(SignalStrength[]::new);
    }

    private BigDecimal[] extractTriggerValues() {
        return Stream.of(indicatorResults)
                .map(EFTResult::getTrigger)
                .toArray(BigDecimal[]::new);
    }

    private SignalStrength toSignalStrength(Signal signal, Strength strength) {
        return nonNull(signal) && signal != NEUTRAL
                ? new SignalStrength(signal, strength)
                : null;
    }

    private SignalStrength[] mergeSignals(SignalStrength[] zeroLineCrossSignals, SignalStrength[] triggerCrossSignals) {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new SignalStrengthMerger().merge(zeroLineCrossSignals[idx], triggerCrossSignals[idx]))
                .toArray(SignalStrength[]::new);
    }

    private void buildEFTAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new EFTAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx]))
                .toArray(EFTAnalyzerResult[]::new);
    }

}
