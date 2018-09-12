package pro.crypto.analyzer.cci;

import pro.crypto.analyzer.helper.StaticLineCrossFinder;
import pro.crypto.analyzer.helper.DefaultDivergenceAnalyzer;
import pro.crypto.analyzer.helper.SignalStrengthMerger;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.cci.CCIResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Strength.NORMAL;
import static pro.crypto.model.Strength.WEAK;

public class CCIAnalyzer implements Analyzer<CCIAnalyzerResult> {

    private final static BigDecimal OVERBOUGHT_LEVEL = new BigDecimal(100);
    private final static BigDecimal OVERSOLD_LEVEL = new BigDecimal(-100);
    private final static BigDecimal ZERO_LEVEL = BigDecimal.ZERO;

    private final Tick[] originalData;
    private final CCIResult[] indicatorResults;

    private CCIAnalyzerResult[] result;

    public CCIAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (CCIResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        SignalStrength[] divergenceSignals = findDivergenceSignals();
        SignalStrength[] crossSignals = findCrossSignals();
        SignalStrength[] mergedSignals = mergeSignal(divergenceSignals, crossSignals);
        buildCCIAnalyzerResult(mergedSignals);
    }

    @Override
    public CCIAnalyzerResult[] getResult() {
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
        SignalStrength[] overboughtSignals = findCrossSignals(indicatorValues, OVERBOUGHT_LEVEL, NORMAL);
        SignalStrength[] oversoldSignals = findCrossSignals(indicatorValues, OVERSOLD_LEVEL, NORMAL);
        SignalStrength[] zeroLineSignals = findCrossSignals(indicatorValues, ZERO_LEVEL, WEAK);
        return mergeSignals(overboughtSignals, oversoldSignals, zeroLineSignals);
    }

    private SignalStrength toSignalStrength(Signal signal, Strength strength) {
        return nonNull(signal)
                ? new SignalStrength(signal, strength)
                : null;
    }

    private SignalStrength[] findCrossSignals(BigDecimal[] indicatorValues, BigDecimal level, Strength strength) {
        return Stream.of(new StaticLineCrossFinder(indicatorValues, level).find())
                .map(signal -> toSignalStrength(signal, strength))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] mergeSignals(SignalStrength[] overboughtSignals, SignalStrength[] oversellSignals, SignalStrength[] zeroLineSignals) {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new SignalStrengthMerger().merge(overboughtSignals[idx], oversellSignals[idx], zeroLineSignals[idx]))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] mergeSignal(SignalStrength[] divergenceSignals, SignalStrength[] crossSignals) {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new SignalStrengthMerger().merge(divergenceSignals[idx], crossSignals[idx]))
                .toArray(SignalStrength[]::new);
    }

    private void buildCCIAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> buildCCIAnalyzerResult(mergedSignals[idx], idx))
                .toArray(CCIAnalyzerResult[]::new);
    }

    private CCIAnalyzerResult buildCCIAnalyzerResult(SignalStrength mergedSignal, int currentIndex) {
        return new CCIAnalyzerResult(indicatorResults[currentIndex].getTime(), mergedSignal, defineSecurityLevel(currentIndex));
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
