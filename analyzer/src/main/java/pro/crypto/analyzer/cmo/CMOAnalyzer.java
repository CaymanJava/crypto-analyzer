package pro.crypto.analyzer.cmo;

import pro.crypto.analyzer.helper.DynamicLineCrossFinder;
import pro.crypto.analyzer.helper.StaticLineCrossFinder;
import pro.crypto.analyzer.helper.DefaultDivergenceAnalyzer;
import pro.crypto.analyzer.helper.SignalStrengthMerger;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.cmo.CMOResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.*;

public class CMOAnalyzer implements Analyzer<CMOAnalyzerResult> {

    private final static BigDecimal OVERBOUGHT_LEVEL = new BigDecimal(50);
    private final static BigDecimal OVERSOLD_LEVEL = new BigDecimal(-50);
    private final static BigDecimal ZERO_LEVEL = ZERO;

    private final Tick[] originalData;
    private final CMOResult[] indicatorResults;

    private CMOAnalyzerResult[] result;

    public CMOAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (CMOResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        SignalStrength[] divergenceSignals = findDivergenceSignals();
        SignalStrength[] crossSignals = findCrossSignals();
        SignalStrength[] mergedSignals = mergeSignals(divergenceSignals, crossSignals);
        buildCMOAnalyzerResult(mergedSignals);
    }

    @Override
    public CMOAnalyzerResult[] getResult() {
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
        BigDecimal[] indicatorValues = IndicatorResultExtractor.extract(indicatorResults);
        SignalStrength[] securityLevelSignals = findSecurityLevelSignals(indicatorValues);
        SignalStrength[] zeroLineSignals = findZeroLineSignals(indicatorValues);
        SignalStrength[] signalLineSignals = findSignalLineSignals(indicatorValues);
        return mergeSignals(securityLevelSignals, zeroLineSignals, signalLineSignals);
    }

    private SignalStrength[] findSecurityLevelSignals(BigDecimal[] indicatorValues) {
        SignalStrength[] oversoldSignals = findOversoldSignals(indicatorValues);
        SignalStrength[] overboughtSignals = findOverboughtSignals(indicatorValues);
        return mergeSignals(oversoldSignals, overboughtSignals);
    }

    private SignalStrength[] findOverboughtSignals(BigDecimal[] indicatorValues) {
        return Stream.of(new StaticLineCrossFinder(indicatorValues, OVERBOUGHT_LEVEL).find())
                .map(signal -> removeFalsePositiveSignal(signal, BUY))
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findOversoldSignals(BigDecimal[] indicatorValues) {
        return Stream.of(new StaticLineCrossFinder(indicatorValues, OVERSOLD_LEVEL).find())
                .map(signal -> removeFalsePositiveSignal(signal, SELL))
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private Signal removeFalsePositiveSignal(Signal signal, Signal falsePositive) {
        return signal != falsePositive ? signal : null;
    }

    private SignalStrength[] findZeroLineSignals(BigDecimal[] indicatorValues) {
        return Stream.of(new StaticLineCrossFinder(indicatorValues, ZERO_LEVEL).find())
                .map(signal -> toSignalStrength(signal, NORMAL))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findSignalLineSignals(BigDecimal[] indicatorValues) {
        return Stream.of(new DynamicLineCrossFinder(indicatorValues, extractSignalLineValues()).find())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private BigDecimal[] extractSignalLineValues() {
        return Stream.of(indicatorResults)
                .map(CMOResult::getSignalLineValue)
                .toArray(BigDecimal[]::new);
    }

    private SignalStrength toSignalStrength(Signal signal, Strength strength) {
        return nonNull(signal) && signal != NEUTRAL
                ? new SignalStrength(signal, strength)
                : null;
    }

    private SignalStrength[] mergeSignals(SignalStrength[] firstSignals, SignalStrength[] secondSignals, SignalStrength[] thirdSignals) {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new SignalStrengthMerger().merge(firstSignals[idx], secondSignals[idx], thirdSignals[idx]))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] mergeSignals(SignalStrength[] firstSignals, SignalStrength[] secondSignals) {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new SignalStrengthMerger().merge(firstSignals[idx], secondSignals[idx]))
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
        if (indicatorValue.compareTo(OVERBOUGHT_LEVEL) >= 0) {
            return SecurityLevel.OVERBOUGHT;
        }

        if (indicatorValue.compareTo(OVERSOLD_LEVEL) <= 0) {
            return SecurityLevel.OVERSOLD;
        }

        return SecurityLevel.NORMAL;
    }

}
