package pro.crypto.analyzer.cci;

import pro.crypto.helper.DefaultDivergenceAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.SecurityLevelAnalyzer;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.cci.CCIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.SecurityLevel;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.analyzer.Strength;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.analyzer.Strength.NORMAL;
import static pro.crypto.model.analyzer.Strength.WEAK;

public class CCIAnalyzer implements Analyzer<CCIAnalyzerResult> {

    private final Tick[] originalData;
    private final CCIResult[] indicatorResults;
    private final BigDecimal oversoldLevel;
    private final BigDecimal overboughtLevel;

    private BigDecimal[] indicatorValues;
    private CCIAnalyzerResult[] result;

    public CCIAnalyzer(AnalyzerRequest analyzerRequest) {
        CCIAnalyzerRequest request = (CCIAnalyzerRequest) analyzerRequest;
        this.originalData = request.getOriginalData();
        this.indicatorResults = (CCIResult[]) request.getIndicatorResults();
        this.oversoldLevel = extractOversoldLevel(request);
        this.overboughtLevel = extractOverboughtLevel(request);
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        SignalStrength[] divergenceSignals = findDivergenceSignals();
        SignalStrength[] crossSignals = findCrossSignals();
        SignalStrength[] mergedSignals = SignalArrayMerger.mergeSignalsStrength(divergenceSignals, crossSignals);
        SecurityLevel[] securityLevels = defineSecurityLevels();
        buildCCIAnalyzerResult(mergedSignals, securityLevels);
    }

    @Override
    public CCIAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractOversoldLevel(CCIAnalyzerRequest request) {
        return ofNullable(request.getOversoldLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(-100));
    }

    private BigDecimal extractOverboughtLevel(CCIAnalyzerRequest request) {
        return ofNullable(request.getOverboughtLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(100));
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private SignalStrength[] findDivergenceSignals() {
        return Stream.of(new DefaultDivergenceAnalyzer(originalData, indicatorValues).analyze())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findCrossSignals() {
        SignalStrength[] overboughtSignals = findCrossSignals(overboughtLevel, NORMAL);
        SignalStrength[] oversoldSignals = findCrossSignals(oversoldLevel, NORMAL);
        SignalStrength[] zeroLineSignals = findCrossSignals(ZERO, WEAK);
        return SignalArrayMerger.mergeSignalsStrength(overboughtSignals, oversoldSignals, zeroLineSignals);
    }

    private SignalStrength[] findCrossSignals(BigDecimal level, Strength strength) {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, level).analyze())
                .map(signal -> toSignalStrength(signal, strength))
                .toArray(SignalStrength[]::new);
    }

    private void buildCCIAnalyzerResult(SignalStrength[] mergedSignals, SecurityLevel[] securityLevels) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> buildCCIAnalyzerResult(mergedSignals[idx], securityLevels[idx], idx))
                .toArray(CCIAnalyzerResult[]::new);
    }

    private CCIAnalyzerResult buildCCIAnalyzerResult(SignalStrength mergedSignal, SecurityLevel securityLevel, int currentIndex) {
        return new CCIAnalyzerResult(indicatorResults[currentIndex].getTime(), mergedSignal, securityLevel);
    }

    private SecurityLevel[] defineSecurityLevels() {
        return new SecurityLevelAnalyzer(indicatorValues, overboughtLevel, oversoldLevel).analyze();
    }

}
