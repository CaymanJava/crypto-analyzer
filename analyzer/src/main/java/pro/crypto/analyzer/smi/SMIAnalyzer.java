package pro.crypto.analyzer.smi;

import pro.crypto.helper.*;
import pro.crypto.indicator.smi.SMIResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.helper.IndicatorResultExtractor.extractSignalLineValues;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Strength.NORMAL;
import static pro.crypto.model.Strength.STRONG;
import static pro.crypto.model.Strength.WEAK;

public class SMIAnalyzer implements Analyzer<SMIAnalyzerResult> {

    private final Tick[] originalData;
    private final SMIResult[] indicatorResults;
    private final BigDecimal oversoldLevel;
    private final BigDecimal overboughtLevel;

    private BigDecimal[] indicatorValues;
    private SMIAnalyzerResult[] result;

    public SMIAnalyzer(AnalyzerRequest analyzerRequest) {
        SMIAnalyzerRequest request = (SMIAnalyzerRequest) analyzerRequest;
        this.originalData = request.getOriginalData();
        this.indicatorResults = (SMIResult[]) request.getIndicatorResults();
        this.oversoldLevel = extractOversoldLevel(request);
        this.overboughtLevel = extractOverboughtLevel(request);
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        SignalStrength[] signals = findSignals();
        SecurityLevel[] securityLevels = findSecurityLevels();
        buildSMIAnalyzerResult(signals, securityLevels);
    }

    @Override
    public SMIAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractOversoldLevel(SMIAnalyzerRequest request) {
        return ofNullable(request.getOversoldLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(-40));
    }

    private BigDecimal extractOverboughtLevel(SMIAnalyzerRequest request) {
        return ofNullable(request.getOverboughtLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(40));
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private SignalStrength[] findSignals() {
        SignalStrength[] divergenceSignals = findDivergenceSignals();
        SignalStrength[] securityLevelSignals = findSecurityLevelSignals();
        SignalStrength[] signalLineCrossSignals = findSignalsLineCrossSignals();
        return mergeSignalsStrength(divergenceSignals, securityLevelSignals, signalLineCrossSignals);
    }

    private SignalStrength[] findDivergenceSignals() {
        return Stream.of(new DefaultDivergenceAnalyzer(originalData, indicatorValues).analyze())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);

    }

    private SignalStrength[] findSecurityLevelSignals() {
        SignalStrength[] oversoldSignals = findOversoldSignals();
        SignalStrength[] overboughtSignals = findOverboughtSignals();
        return mergeSignalsStrength(overboughtSignals, oversoldSignals);
    }

    private SignalStrength[] findOversoldSignals() {
        return Stream.of(
                new StaticLineCrossAnalyzer(indicatorValues, oversoldLevel)
                        .withRemovingFalsePositive(SELL)
                        .analyze())
                .map(signal -> toSignalStrength(signal, NORMAL))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findOverboughtSignals() {
        return Stream.of(
                new StaticLineCrossAnalyzer(indicatorValues, overboughtLevel)
                        .withRemovingFalsePositive(BUY)
                        .analyze())
                .map(signal -> toSignalStrength(signal, NORMAL))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findSignalsLineCrossSignals() {
        return Stream.of(new DynamicLineCrossAnalyzer(indicatorValues, extractSignalLineValues(indicatorResults)).analyze())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SecurityLevel[] findSecurityLevels() {
        return new SecurityLevelAnalyzer(indicatorValues, overboughtLevel, oversoldLevel).analyze();
    }

    private void buildSMIAnalyzerResult(SignalStrength[] signals, SecurityLevel[] securityLevels) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new SMIAnalyzerResult(indicatorResults[idx].getTime(), signals[idx], securityLevels[idx]))
                .toArray(SMIAnalyzerResult[]::new);
    }

}
