package pro.crypto.analyzer.uo;

import pro.crypto.helper.DefaultDivergenceAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.SecurityLevelAnalyzer;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.uo.UOResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.SecurityLevel;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.analyzer.Signal.BUY;
import static pro.crypto.model.analyzer.Signal.SELL;
import static pro.crypto.model.analyzer.Strength.NORMAL;
import static pro.crypto.model.analyzer.Strength.STRONG;
import static pro.crypto.model.analyzer.Strength.WEAK;

public class UOAnalyzer implements Analyzer<UOAnalyzerResult> {

    private final Tick[] originalData;
    private final UOResult[] indicatorResults;
    private final BigDecimal oversoldLevel;
    private final BigDecimal overboughtLevel;
    private final BigDecimal middleLevel;

    private BigDecimal[] indicatorValues;
    private UOAnalyzerResult[] result;

    public UOAnalyzer(AnalyzerRequest analyzerRequest) {
        UOAnalyzerRequest request = (UOAnalyzerRequest) analyzerRequest;
        this.originalData = request.getOriginalData();
        this.indicatorResults = (UOResult[]) request.getIndicatorResults();
        this.oversoldLevel = extractOversoldLevel(request);
        this.overboughtLevel = extractOverboughtLevel(request);
        this.middleLevel = calculateMiddleLevel();
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        SignalStrength[] divergenceSignals = findDivergenceSignals();
        SignalStrength[] securityLevelCrossSignals = findSecurityLevelsCrossSignals();
        SignalStrength[] middleLineCrossSignals = findMiddleLineCrossSignals();
        SignalStrength[] mergedSignals = SignalArrayMerger.mergeSignalsStrength(divergenceSignals, securityLevelCrossSignals, middleLineCrossSignals);
        SecurityLevel[] securityLevels = defineSecurityLevels();
        buildUOAnalyzerResult(mergedSignals, securityLevels);
    }

    @Override
    public UOAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractOversoldLevel(UOAnalyzerRequest request) {
        return ofNullable(request.getOversoldLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(30));
    }

    private BigDecimal extractOverboughtLevel(UOAnalyzerRequest request) {
        return ofNullable(request.getOverboughtLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(70));
    }

    private BigDecimal calculateMiddleLevel() {
        return MathHelper.average(overboughtLevel, oversoldLevel);
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private SignalStrength[] findDivergenceSignals() {
        return Stream.of(new DefaultDivergenceAnalyzer(originalData, indicatorValues).analyze())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findSecurityLevelsCrossSignals() {
        SignalStrength[] oversoldSignals = findOversoldSignals();
        SignalStrength[] overboughtSignals = findOverboughtSignals();
        return SignalArrayMerger.mergeSignalsStrength(oversoldSignals, overboughtSignals);
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

    private SignalStrength[] findMiddleLineCrossSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, middleLevel).analyze())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SecurityLevel[] defineSecurityLevels() {
        return new SecurityLevelAnalyzer(indicatorValues, overboughtLevel, oversoldLevel).analyze();
    }

    private void buildUOAnalyzerResult(SignalStrength[] mergedSignals, SecurityLevel[] securityLevels) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new UOAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx], securityLevels[idx]))
                .toArray(UOAnalyzerResult[]::new);
    }

}
