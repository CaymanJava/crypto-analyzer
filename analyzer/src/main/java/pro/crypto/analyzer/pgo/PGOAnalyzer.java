package pro.crypto.analyzer.pgo;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.pgo.PGOResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.Signal;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.analyzer.Strength;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.analyzer.Signal.BUY;
import static pro.crypto.model.analyzer.Signal.SELL;
import static pro.crypto.model.analyzer.Strength.STRONG;
import static pro.crypto.model.analyzer.Strength.WEAK;

public class PGOAnalyzer implements Analyzer<PGOAnalyzerResult> {

    private final Tick[] originalData;
    private final PGOResult[] indicatorResults;
    private final BigDecimal oversoldLevel;
    private final BigDecimal overboughtLevel;

    private BigDecimal[] indicatorValues;
    private PGOAnalyzerResult[] result;

    public PGOAnalyzer(AnalyzerRequest analyzerRequest) {
        PGOAnalyzerRequest request = (PGOAnalyzerRequest) analyzerRequest;
        this.originalData = request.getOriginalData();
        this.indicatorResults = (PGOResult[]) request.getIndicatorResults();
        this.oversoldLevel = extractOversoldLevel(request);
        this.overboughtLevel = extractOverboughtLevel(request);
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        SignalStrength[] overboughtLevelSignals = findOverboughtLevelSignals();
        SignalStrength[] oversoldLeverSignals = findOversoldLevelSignals();
        SignalStrength[] zeroLineCrossSignals = findZeroLineCrossSignals();
        SignalStrength[] mergedSignals = SignalArrayMerger.mergeSignalsStrength(overboughtLevelSignals, oversoldLeverSignals, zeroLineCrossSignals);
        buildPGOAnalyzerResult(mergedSignals);
    }

    @Override
    public PGOAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractOversoldLevel(PGOAnalyzerRequest request) {
        return ofNullable(request.getOversoldLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(-3));
    }

    private BigDecimal extractOverboughtLevel(PGOAnalyzerRequest request) {
        return ofNullable(request.getOverboughtLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(3));
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private SignalStrength[] findOverboughtLevelSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, overboughtLevel).analyze())
                .map(signal -> toSignalStrength(signal, defineOverboughtStrength(signal)))
                .toArray(SignalStrength[]::new);
    }

    private Strength defineOverboughtStrength(Signal signal) {
        if (signal == BUY) {
            return STRONG;
        }

        if (signal == SELL) {
            return WEAK;
        }

        return null;
    }

    private SignalStrength[] findOversoldLevelSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, oversoldLevel).analyze())
                .map(signal -> toSignalStrength(signal, defineOversoldStrength(signal)))
                .toArray(SignalStrength[]::new);
    }

    private Strength defineOversoldStrength(Signal signal) {
        if (signal == SELL) {
            return STRONG;
        }

        if (signal == BUY) {
            return WEAK;
        }

        return null;
    }

    private SignalStrength[] findZeroLineCrossSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, BigDecimal.ZERO).analyze())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private void buildPGOAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new PGOAnalyzerResult(originalData[idx].getTickTime(), mergedSignals[idx]))
                .toArray(PGOAnalyzerResult[]::new);
    }

}
