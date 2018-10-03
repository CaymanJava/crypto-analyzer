package pro.crypto.analyzer.cmf;

import pro.crypto.helper.DefaultDivergenceAnalyzer;
import pro.crypto.helper.StaticLineCrossFinder;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.cmf.CMFResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;
import pro.crypto.model.Trend;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
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
        Signal[] divergenceSignals = findDivergenceSignals();
        Signal[] intersectionSignals = findIntersectionSignals();
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

    private Signal[] findDivergenceSignals() {
        return new DefaultDivergenceAnalyzer().analyze(originalData, IndicatorResultExtractor.extractIndicatorValue(indicatorResults));
    }

    private Signal[] findIntersectionSignals() {
        BigDecimal[] indicatorValues = IndicatorResultExtractor.extractIndicatorValue(indicatorResults);
        Signal[] buySignals = findBuySignals(indicatorValues);
        Signal[] sellSignals = findSellSignals(indicatorValues);
        return mergeSignals(buySignals, sellSignals);
    }

    private Signal[] findBuySignals(BigDecimal[] indicatorValues) {
        return Stream.of(new StaticLineCrossFinder(indicatorValues, BULLISH_SIGNAL_LINE).find())
                .map(signal -> removeFalsePositiveSignal(signal, SELL))
                .toArray(Signal[]::new);
    }

    private Signal[] findSellSignals(BigDecimal[] indicatorValues) {
        return Stream.of(new StaticLineCrossFinder(indicatorValues, BEARER_SIGNAL_LINE).find())
                .map(signal -> removeFalsePositiveSignal(signal, BUY))
                .toArray(Signal[]::new);
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
