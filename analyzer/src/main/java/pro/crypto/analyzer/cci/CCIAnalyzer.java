package pro.crypto.analyzer.cci;

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
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.*;

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
                .map(this::toSignalStrength)
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength toSignalStrength(Signal signal) {
        return nonNull(signal)
                ? new SignalStrength(signal, WEAK)
                : null;
    }

    private SignalStrength[] findCrossSignals() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::findCrossSignal)
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength findCrossSignal(int currentIndex) {
        return isPossibleDefineSignal(currentIndex)
                ? defineCrossSignal(currentIndex)
                : null;
    }

    private boolean isPossibleDefineSignal(int currentIndex) {
        return currentIndex > 0
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getIndicatorValue());
    }

    private SignalStrength defineCrossSignal(int currentIndex) {
        if (isOverboughtIntersection(currentIndex)) {
            return new SignalStrength(defineOverboughtSignal(currentIndex), NORMAL);
        }

        if (isOversoldIntersection(currentIndex)) {
            return new SignalStrength(defineOverSoldSignal(currentIndex), NORMAL);
        }

        if (isZeroLineIntersection(currentIndex)) {
            return new SignalStrength(defineZeroLineSignal(currentIndex), WEAK);
        }

        return null;
    }

    private boolean isOverboughtIntersection(int currentIndex) {
        return isIntersection(OVERBOUGHT_LEVEL, currentIndex);
    }

    private Signal defineOverboughtSignal(int currentIndex) {
        return isCrossDownUpOverbought(currentIndex) ? BUY : SELL;
    }

    private boolean isCrossDownUpOverbought(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(OVERBOUGHT_LEVEL) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(OVERBOUGHT_LEVEL) >= 0;
    }

    private boolean isOversoldIntersection(int currentIndex) {
        return isIntersection(OVERSOLD_LEVEL, currentIndex);
    }

    private Signal defineOverSoldSignal(int currentIndex) {
        return isCrossUpDownOversold(currentIndex) ? BUY : SELL;
    }

    private boolean isCrossUpDownOversold(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(OVERSOLD_LEVEL) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(OVERSOLD_LEVEL) >= 0;
    }

    private boolean isZeroLineIntersection(int currentIndex) {
        return isIntersection(ZERO_LEVEL, currentIndex);
    }

    private Signal defineZeroLineSignal(int currentIndex) {
        return isCrossDownUpZeroLine(currentIndex) ? BUY : SELL;
    }

    private boolean isCrossDownUpZeroLine(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(ZERO_LEVEL) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO_LEVEL) >= 0;
    }

    private boolean isIntersection(BigDecimal intersectionLevel, int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(intersectionLevel)
                != indicatorResults[currentIndex].getIndicatorValue().compareTo(intersectionLevel);
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
