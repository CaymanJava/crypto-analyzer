package pro.crypto.analyzer.stoch;

import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.helper.SecurityLevelAnalyzer;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.stoch.StochResult;
import pro.crypto.model.*;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Strength.WEAK;

public class StochAnalyzer implements Analyzer<StochAnalyzerResult> {

    private final StochResult[] indicatorResults;
    private final BigDecimal oversoldLevel;
    private final BigDecimal overboughtLevel;

    private BigDecimal[] fastStochasticValues;
    private BigDecimal[] slowStochasticValues;
    private StochAnalyzerResult[] result;

    public StochAnalyzer(AnalyzerRequest analyzerRequest) {
        StochAnalyzerRequest request = (StochAnalyzerRequest) analyzerRequest;
        this.indicatorResults = (StochResult[]) request.getIndicatorResults();
        this.oversoldLevel = extractOversoldLevel(request);
        this.overboughtLevel = extractOverboughtLevel(request);
    }

    @Override
    public void analyze() {
        fastStochasticValues = extractStochasticValues(StochResult::getFastStochastic);
        slowStochasticValues = extractStochasticValues(StochResult::getSlowStochastic);
        SignalStrength[] securityLevelCrossSignals = findSecurityLevelCrossSignals();
        SignalStrength[] stochasticsCrossSignals = findStochasticCrossSignals();
        SignalStrength[] mergedSignals = SignalArrayMerger.mergeSignalsStrength(securityLevelCrossSignals, stochasticsCrossSignals);
        SecurityLevel[] securityLevels = findSecurityLevels();
        buildStochAnalyzerResult(mergedSignals, securityLevels);
    }

    @Override
    public StochAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractOversoldLevel(StochAnalyzerRequest request) {
        return ofNullable(request.getOversoldLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(20));
    }

    private BigDecimal extractOverboughtLevel(StochAnalyzerRequest request) {
        return ofNullable(request.getOverboughtLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(80));
    }

    private BigDecimal[] extractStochasticValues(Function<StochResult, BigDecimal> extractFunction) {
        return Stream.of(indicatorResults)
                .map(extractFunction)
                .toArray(BigDecimal[]::new);
    }

    private SignalStrength[] findSecurityLevelCrossSignals() {
        SignalStrength[] oversoldCrossSignals = findOversoldCrossSignals();
        SignalStrength[] overboughtCrossSignals = findOverboughtCrossSignals();
        return SignalArrayMerger.mergeSignalsStrength(oversoldCrossSignals, overboughtCrossSignals);
    }

    private SignalStrength[] findOversoldCrossSignals() {
        SignalStrength[] fastStochasticCrossSignals = findStochasticOversoldCrossSignals(fastStochasticValues);
        SignalStrength[] slowStochasticCrossSignals = findStochasticOversoldCrossSignals(slowStochasticValues);
        return SignalArrayMerger.mergeSignalsStrength(fastStochasticCrossSignals, slowStochasticCrossSignals);
    }

    private SignalStrength[] findStochasticOversoldCrossSignals(BigDecimal[] stochasticValues) {
        return Stream.of(
                new StaticLineCrossAnalyzer(stochasticValues, oversoldLevel)
                        .withRemovingFalsePositive(SELL)
                        .analyze())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findOverboughtCrossSignals() {
        SignalStrength[] fastStochasticCrossSignals = findStochasticOverboughtCrossSignals(fastStochasticValues);
        SignalStrength[] slowStochasticCrossSignals = findStochasticOverboughtCrossSignals(slowStochasticValues);
        return SignalArrayMerger.mergeSignalsStrength(fastStochasticCrossSignals, slowStochasticCrossSignals);
    }

    private SignalStrength[] findStochasticOverboughtCrossSignals(BigDecimal[] stochasticValues) {
        return Stream.of(
                new StaticLineCrossAnalyzer(stochasticValues, oversoldLevel)
                        .withRemovingFalsePositive(BUY)
                        .analyze())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findStochasticCrossSignals() {
        return Stream.of(new DynamicLineCrossAnalyzer(fastStochasticValues, slowStochasticValues).analyze())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SecurityLevel[] findSecurityLevels() {
        return new SecurityLevelAnalyzer(slowStochasticValues, overboughtLevel, oversoldLevel).analyze();
    }

    private void buildStochAnalyzerResult(SignalStrength[] mergedSignals, SecurityLevel[] securityLevels) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new StochAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx], securityLevels[idx]))
                .toArray(StochAnalyzerResult[]::new);
    }

}
