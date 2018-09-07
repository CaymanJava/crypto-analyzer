package pro.crypto.analyzer.cmf;

import pro.crypto.analyzer.helper.divergence.Divergence;
import pro.crypto.analyzer.helper.divergence.DivergenceRequest;
import pro.crypto.analyzer.helper.divergence.DivergenceResult;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.cmf.CMFResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Trend.*;

public class CMFAnalyzer implements Analyzer<CMFAnalyzerResult> {

    private final static BigDecimal BULLISH_SIGNAL_LINE = new BigDecimal(0.05);
    private final static BigDecimal BEARER_SIGNAL_LINE = new BigDecimal(-0.05);

    private final Tick[] originalData;
    private final CMFResult[] indicatorResults;

    private CMFAnalyzerResult[] result;

    public CMFAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (CMFResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] intersectionSignals = findIntersectionSignals();
        Signal[] divergenceSignals = findDivergenceSignals();
        Signal[] mergedSignals = mergeSignals(intersectionSignals, divergenceSignals);
        Trend[] trends = defineTrends();
        buildCMFAnalyzerResult(mergedSignals, trends);
    }

    @Override
    public CMFAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findIntersectionSignals() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::tryDefineIntersectionSignal)
                .toArray(Signal[]::new);
    }

    private Signal tryDefineIntersectionSignal(int currentIndex) {
        return isPossibleDefineSignal(currentIndex)
                ? defineIntersectionSignal(currentIndex)
                : NEUTRAL;
    }

    private boolean isPossibleDefineSignal(int currentIndex) {
        return currentIndex > 0
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getIndicatorValue());
    }

    private Signal defineIntersectionSignal(int currentIndex) {
        if (isBuyIntersection(currentIndex)) {
            return BUY;
        }

        if (isSellIntersection(currentIndex)) {
            return SELL;
        }

        return NEUTRAL;
    }

    private boolean isBuyIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(BULLISH_SIGNAL_LINE) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(BULLISH_SIGNAL_LINE) >= 0;
    }

    private boolean isSellIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(BEARER_SIGNAL_LINE) > 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(BEARER_SIGNAL_LINE) <= 0;
    }

    private Signal[] findDivergenceSignals() {
        DivergenceResult[] divergences = new Divergence(buildDivergenceRequest()).find();
        Signal[] signals = new Signal[indicatorResults.length];
        Stream.of(divergences)
                .filter(this::isPriceExist)
                .forEach(divergence -> signals[divergence.getIndexTo() + 1] = divergence.recognizeSignal());
        return signals;
    }

    private boolean isPriceExist(DivergenceResult divergence) {
        return divergence.getIndexTo() < originalData.length;
    }

    private DivergenceRequest buildDivergenceRequest() {
        return DivergenceRequest.builder()
                .originalData(originalData)
                .indicatorValues(IndicatorResultExtractor.extract(indicatorResults))
                .build();
    }

    private Signal[] mergeSignals(Signal[] intersectionSignals, Signal[] divergenceSignals) {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> mergeSignals(intersectionSignals[idx], divergenceSignals[idx]))
                .toArray(Signal[]::new);
    }

    private Signal mergeSignals(Signal intersectionSignal, Signal divergenceSignal) {
        if (nonNull(intersectionSignal) && nonNull(divergenceSignal)) {
            return tryMergeSignals(intersectionSignal, divergenceSignal);
        }

        if (nonNull(divergenceSignal)) {
            return divergenceSignal;
        }

        if (nonNull(intersectionSignal)) {
            return intersectionSignal;
        }

        return NEUTRAL;
    }

    private Signal tryMergeSignals(Signal intersectionSignal, Signal divergenceSignal) {
        return intersectionSignal == divergenceSignal
                ? intersectionSignal
                : divergenceSignal;
    }

    private Trend[] defineTrends() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::defineTrend)
                .toArray(Trend[]::new);
    }

    private Trend defineTrend(int currentIndex) {
        return isPossibleDefineTrend(currentIndex)
                ? defineTrend(indicatorResults[currentIndex].getIndicatorValue())
                : UNDEFINED;
    }

    private boolean isPossibleDefineTrend(int currentIndex) {
        return nonNull(indicatorResults[currentIndex].getIndicatorValue());
    }

    private Trend defineTrend(BigDecimal indicatorValue) {
        if (isConsolidationZone(indicatorValue)) {
            return CONSOLIDATION;
        }

        if (isBullishZone(indicatorValue)) {
            return UPTREND;
        }

        if (isBearerZone(indicatorValue)) {
            return DOWNTREND;
        }

        return UNDEFINED;
    }

    private boolean isConsolidationZone(BigDecimal indicatorValue) {
        return indicatorValue.compareTo(BULLISH_SIGNAL_LINE) <= 0
                && indicatorValue.compareTo(BEARER_SIGNAL_LINE) >= 0;
    }

    private boolean isBullishZone(BigDecimal indicatorValue) {
        return indicatorValue.compareTo(BULLISH_SIGNAL_LINE) > 0;
    }

    private boolean isBearerZone(BigDecimal indicatorValue) {
        return indicatorValue.compareTo(BEARER_SIGNAL_LINE) < 0;
    }

    private void buildCMFAnalyzerResult(Signal[] mergedSignals, Trend[] trends) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new CMFAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx], trends[idx]))
                .toArray(CMFAnalyzerResult[]::new);
    }

}
