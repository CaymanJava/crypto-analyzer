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
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
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

    private SignalStrength[] findDivergenceSignals() {
        return Stream.of(new DefaultDivergenceAnalyzer().analyze(originalData, IndicatorResultExtractor.extractIndicatorValues(indicatorResults)))
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findCrossSignals() {
        BigDecimal[] indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
        SignalStrength[] securityLevelSignals = findSecurityLevelSignals(indicatorValues);
        SignalStrength[] zeroLineSignals = findZeroLineSignals(indicatorValues);
        SignalStrength[] signalLineSignals = findSignalLineSignals(indicatorValues);
        return mergeSignalsStrength(securityLevelSignals, zeroLineSignals, signalLineSignals);
    }

    private SignalStrength[] findSecurityLevelSignals(BigDecimal[] indicatorValues) {
        SignalStrength[] oversoldSignals = findOversoldSignals(indicatorValues);
        SignalStrength[] overboughtSignals = findOverboughtSignals(indicatorValues);
        return mergeSignalsStrength(oversoldSignals, overboughtSignals);
    }

    private SignalStrength[] findOverboughtSignals(BigDecimal[] indicatorValues) {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, OVERBOUGHT_LEVEL).analyze())
                .map(signal -> removeFalsePositiveSignal(signal, BUY))
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findOversoldSignals(BigDecimal[] indicatorValues) {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, OVERSOLD_LEVEL).analyze())
                .map(signal -> removeFalsePositiveSignal(signal, SELL))
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findZeroLineSignals(BigDecimal[] indicatorValues) {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, ZERO_LEVEL).analyze())
                .map(signal -> toSignalStrength(signal, NORMAL))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findSignalLineSignals(BigDecimal[] indicatorValues) {
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
        if (indicatorValue.compareTo(OVERBOUGHT_LEVEL) >= 0) {
            return SecurityLevel.OVERBOUGHT;
        }

        if (indicatorValue.compareTo(OVERSOLD_LEVEL) <= 0) {
            return SecurityLevel.OVERSOLD;
        }

        return SecurityLevel.NORMAL;
    }

}
