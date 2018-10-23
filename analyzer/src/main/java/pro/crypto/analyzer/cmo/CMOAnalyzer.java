package pro.crypto.analyzer.cmo;

import pro.crypto.helper.DefaultDivergenceAnalyzer;
import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.cmo.CMOResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.SecurityLevel;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Strength.*;

public class CMOAnalyzer implements Analyzer<CMOAnalyzerResult> {

    private final Tick[] originalData;
    private final CMOResult[] indicatorResults;
    private final BigDecimal oversoldLevel;
    private final BigDecimal overboughtLevel;

    private BigDecimal[] indicatorValues;
    private CMOAnalyzerResult[] result;

    public CMOAnalyzer(AnalyzerRequest analyzerRequest) {
        CMOAnalyzerRequest request = (CMOAnalyzerRequest) analyzerRequest;
        this.originalData = request.getOriginalData();
        this.indicatorResults = (CMOResult[]) request.getIndicatorResults();
        this.oversoldLevel = extractOversoldLevel(request);
        this.overboughtLevel = extractOverboughtLevel(request);
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        SignalStrength[] divergenceSignals = findDivergenceSignals();
        SignalStrength[] crossSignals = findCrossSignals();
        SignalStrength[] mergedSignals = mergeSignalsStrength(divergenceSignals, crossSignals);
        buildCMOAnalyzerResult(mergedSignals);
    }

    @Override
    public CMOAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private BigDecimal extractOversoldLevel(CMOAnalyzerRequest request) {
        return ofNullable(request.getOversoldLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(-50));
    }

    private BigDecimal extractOverboughtLevel(CMOAnalyzerRequest request) {
        return ofNullable(request.getOverboughtLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(50));
    }

    private SignalStrength[] findDivergenceSignals() {
        return Stream.of(new DefaultDivergenceAnalyzer(originalData, indicatorValues).analyze())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findCrossSignals() {
        SignalStrength[] securityLevelSignals = findSecurityLevelSignals();
        SignalStrength[] zeroLineSignals = findZeroLineSignals();
        SignalStrength[] signalLineSignals = findSignalLineSignals();
        return mergeSignalsStrength(securityLevelSignals, zeroLineSignals, signalLineSignals);
    }

    private SignalStrength[] findSecurityLevelSignals() {
        SignalStrength[] oversoldSignals = findOversoldSignals();
        SignalStrength[] overboughtSignals = findOverboughtSignals();
        return mergeSignalsStrength(oversoldSignals, overboughtSignals);
    }

    private SignalStrength[] findOverboughtSignals() {
        return Stream.of(
                new StaticLineCrossAnalyzer(indicatorValues, overboughtLevel)
                        .withRemovingFalsePositive(BUY)
                        .analyze())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findOversoldSignals() {
        return Stream.of(
                new StaticLineCrossAnalyzer(indicatorValues, oversoldLevel)
                        .withRemovingFalsePositive(SELL)
                        .analyze())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findZeroLineSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, ZERO).analyze())
                .map(signal -> toSignalStrength(signal, NORMAL))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findSignalLineSignals() {
        return Stream.of(new DynamicLineCrossAnalyzer(indicatorValues, IndicatorResultExtractor.extractSignalLineValues(indicatorResults)).analyze())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private void buildCMOAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new CMOAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx], defineSecurityLevel(idx)))
                .toArray(CMOAnalyzerResult[]::new);
    }

    private SecurityLevel defineSecurityLevel(int currentIndex) {
        return isPossibleDefineSecurityLevel(currentIndex)
                ? defineSecurityLevel(indicatorResults[currentIndex].getIndicatorValue())
                : SecurityLevel.UNDEFINED;
    }

    private boolean isPossibleDefineSecurityLevel(int currentIndex) {
        return nonNull(indicatorResults[currentIndex].getIndicatorValue());
    }

    private SecurityLevel defineSecurityLevel(BigDecimal indicatorValue) {
        if (indicatorValue.compareTo(overboughtLevel) >= 0) {
            return SecurityLevel.OVERBOUGHT;
        }

        if (indicatorValue.compareTo(oversoldLevel) <= 0) {
            return SecurityLevel.OVERSOLD;
        }

        return SecurityLevel.NORMAL;
    }

}
