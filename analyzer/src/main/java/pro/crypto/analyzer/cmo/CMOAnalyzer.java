package pro.crypto.analyzer.cmo;

import pro.crypto.analyzer.helper.divergence.Divergence;
import pro.crypto.analyzer.helper.divergence.DivergenceRequest;
import pro.crypto.analyzer.helper.divergence.DivergenceResult;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.cmo.CMOResult;
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

public class CMOAnalyzer implements Analyzer<CMOAnalyzerResult> {

    private final static BigDecimal OVERBOUGHT_LEVEL = new BigDecimal(50);
    private final static BigDecimal OVERSOLD_LEVEL = new BigDecimal(-50);
    private final static BigDecimal ZERO_LEVEL = BigDecimal.ZERO;

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
        SignalStrength[] mergedSignals = mergeSignalStrengths(divergenceSignals, crossSignals);
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
        DivergenceResult[] divergences = new Divergence(buildDivergenceRequest()).find();
        SignalStrength[] signalStrengths = new SignalStrength[indicatorResults.length];
        Stream.of(divergences)
                .filter(this::isPriceExist)
                .forEach(divergence -> signalStrengths[divergence.getIndexTo() + 1] = new SignalStrength(divergence.recognizeSignal(), WEAK));
        return signalStrengths;
    }

    private DivergenceRequest buildDivergenceRequest() {
        return DivergenceRequest.builder()
                .originalData(originalData)
                .indicatorValues(IndicatorResultExtractor.extract(indicatorResults))
                .build();
    }

    private boolean isPriceExist(DivergenceResult divergence) {
        return divergence.getIndexTo() < originalData.length;
    }

    private SignalStrength[] findCrossSignals() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::tryDefineCrossSignal)
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength tryDefineCrossSignal(int currentIndex) {
        return isPossibleDefineCrossSignal(currentIndex)
                ? defineCrossSignal(currentIndex)
                : null;
    }

    private boolean isPossibleDefineCrossSignal(int currentIndex) {
        return currentIndex > 0
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getIndicatorValue());
    }

    private SignalStrength defineCrossSignal(int currentIndex) {
        if (isOversoldIntersection(currentIndex)) {
            return new SignalStrength(BUY, STRONG);
        }

        if (isOverboughtIntersection(currentIndex)) {
            return new SignalStrength(SELL, STRONG);
        }

        if (isZeroLineIntersection(currentIndex)) {
            return new SignalStrength(defineZeroLineSignal(currentIndex), NORMAL);
        }

        if (isPossibleDefineSignalLineIntersection(currentIndex) && isSignalLineIntersection(currentIndex)) {
            return new SignalStrength(defineSignalLineSignal(currentIndex), WEAK);
        }

        return null;
    }

    private boolean isOversoldIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(OVERSOLD_LEVEL) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(OVERSOLD_LEVEL) >= 0;
    }

    private boolean isOverboughtIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(OVERBOUGHT_LEVEL) > 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(OVERBOUGHT_LEVEL) <= 0;
    }

    private boolean isZeroLineIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(ZERO_LEVEL)
                != indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO_LEVEL);
    }

    private Signal defineZeroLineSignal(int currentIndex) {
        return isCrossDownUpZeroLine(currentIndex) ? BUY : SELL;
    }

    private boolean isCrossDownUpZeroLine(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(ZERO_LEVEL) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO_LEVEL) >= 0;
    }

    private boolean isPossibleDefineSignalLineIntersection(int currentIndex) {
        return nonNull(indicatorResults[currentIndex - 1].getSignalLineValue())
                && nonNull(indicatorResults[currentIndex].getSignalLineValue());
    }

    private boolean isSignalLineIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getSignalLineValue())
                != indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex].getSignalLineValue());
    }

    private Signal defineSignalLineSignal(int currentIndex) {
        return isCrossDownUpSignalLine(currentIndex) ? BUY : SELL;
    }

    private boolean isCrossDownUpSignalLine(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getSignalLineValue()) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex].getSignalLineValue()) >= 0;
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
        switch (crossSignal.getStrength()) {
            case WEAK:
                return NORMAL;
            case NORMAL:
                return STRONG;
            default:
                return STRONG;
        }
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
