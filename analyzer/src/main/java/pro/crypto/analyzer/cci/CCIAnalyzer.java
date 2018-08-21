package pro.crypto.analyzer.cci;

import pro.crypto.analyzer.helper.divergence.Divergence;
import pro.crypto.analyzer.helper.divergence.DivergenceRequest;
import pro.crypto.analyzer.helper.divergence.DivergenceResult;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.cci.CCIResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.NEUTRAL;
import static pro.crypto.model.Signal.SELL;
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
        DivergenceResult[] divergences = new Divergence(buildDivergenceRequest()).find();
        SignalStrength[] divergenceSignals = findDivergenceSignals(divergences);
        SignalStrength[] crossSignals = findCrossSignals();
        SignalStrength[] mergedSignals = mergeSignalStrengths(divergenceSignals, crossSignals);
        buildCCIAnalyzerResult(mergedSignals);
    }

    @Override
    public CCIAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private DivergenceRequest buildDivergenceRequest() {
        return DivergenceRequest.builder()
                .originalData(originalData)
                .indicatorValues(IndicatorResultExtractor.extract(indicatorResults))
                .build();
    }

    private SignalStrength[] findDivergenceSignals(DivergenceResult[] divergences) {
        SignalStrength[] signalStrengths = new SignalStrength[indicatorResults.length];
        Stream.of(divergences)
                .filter(this::isPriceExist)
                .forEach(divergence -> signalStrengths[divergence.getIndexTo() + 1] = new SignalStrength(recognizeSignal(divergence), WEAK));
        return signalStrengths;
    }

    private boolean isPriceExist(DivergenceResult divergence) {
        return divergence.getIndexTo() < originalData.length;
    }

    private Signal recognizeSignal(DivergenceResult divergence) {
        switch (divergence.getDivergenceType()) {
            case BEARER:
                return SELL;
            case BULLISH:
                return BUY;
            default:
                return null;
        }
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
        return isCrossDownUpZero(currentIndex) ? BUY : SELL;
    }

    private boolean isCrossDownUpZero(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(ZERO_LEVEL) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO_LEVEL) >= 0;
    }

    private boolean isIntersection(BigDecimal intersectionLevel, int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(intersectionLevel)
                != indicatorResults[currentIndex].getIndicatorValue().compareTo(intersectionLevel);
    }

    private SignalStrength[] mergeSignalStrengths(SignalStrength[] divergenceSignals, SignalStrength[] crossSignals) {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> mergeSignalStrengths(divergenceSignals[idx], crossSignals[idx]))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength mergeSignalStrengths(SignalStrength divergenceSignal, SignalStrength crossSignal) {
        if (nonNull(divergenceSignal) && nonNull(crossSignal)) {
            return tryMergeSignals(divergenceSignal, crossSignal);
        }
        if (nonNull(crossSignal)) {
            return crossSignal;
        }
        if (nonNull(divergenceSignal)) {
            return divergenceSignal;
        }
        return new SignalStrength(NEUTRAL, UNDEFINED);
    }

    private SignalStrength tryMergeSignals(SignalStrength divergenceSignal, SignalStrength crossSignal) {
        return signalsTheSame(divergenceSignal, crossSignal)
                ? mergeSameSignals(crossSignal)
                : mergeDifferentSignals(crossSignal);
    }

    private boolean signalsTheSame(SignalStrength divergenceSignal, SignalStrength crossSignal) {
        return divergenceSignal.getSignal() == crossSignal.getSignal();
    }

    private SignalStrength mergeSameSignals(SignalStrength crossSignal) {
        return new SignalStrength(crossSignal.getSignal(), defineStrength(crossSignal));
    }

    private SignalStrength mergeDifferentSignals(SignalStrength crossSignal) {
        return crossSignal.getStrength() == WEAK
                ? new SignalStrength(NEUTRAL, NORMAL)
                : crossSignal;
    }

    private Strength defineStrength(SignalStrength crossSignal) {
        return crossSignal.getStrength() == WEAK ? NORMAL : STRONG;
    }

    private void buildCCIAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> buildCCIAnalyzerResult(mergedSignals[idx], idx))
                .toArray(CCIAnalyzerResult[]::new);
    }

    private CCIAnalyzerResult buildCCIAnalyzerResult(SignalStrength mergedSignal, int currentIndex) {
        return new CCIAnalyzerResult(
                originalData[currentIndex].getTickTime(), mergedSignal.getSignal(), mergedSignal.getStrength(),
                indicatorResults[currentIndex].getIndicatorValue(), originalData[currentIndex].getClose(), defineSecurityLevel(currentIndex)
        );
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
