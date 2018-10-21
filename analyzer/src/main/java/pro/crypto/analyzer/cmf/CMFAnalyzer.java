package pro.crypto.analyzer.cmf;

import pro.crypto.helper.DefaultDivergenceAnalyzer;
import pro.crypto.helper.StaticLineCrossAnalyzer;
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
import static java.util.Optional.ofNullable;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Trend.*;

public class CMFAnalyzer implements Analyzer<CMFAnalyzerResult> {

    private final BigDecimal bullishSignalLine;
    private final BigDecimal bearerSignalLine;

    private final Tick[] originalData;
    private final CMFResult[] indicatorResults;

    private BigDecimal[] indicatorValues;
    private CMFAnalyzerResult[] result;

    public CMFAnalyzer(AnalyzerRequest analyzerRequest) {
        CMFAnalyzerRequest request = (CMFAnalyzerRequest) analyzerRequest;
        this.originalData = request.getOriginalData();
        this.indicatorResults = (CMFResult[]) request.getIndicatorResults();
        this.bullishSignalLine = extractBullishSignalLine(request);
        this.bearerSignalLine = extractBearerSignalLine(request);
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
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

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private BigDecimal extractBullishSignalLine(CMFAnalyzerRequest request) {
        return ofNullable(request.getBullishSignalLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(0.05));
    }

    private BigDecimal extractBearerSignalLine(CMFAnalyzerRequest request) {
        return ofNullable(request.getBearerSignalLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(-0.05));
    }

    private Signal[] findDivergenceSignals() {
        return new DefaultDivergenceAnalyzer(originalData, indicatorValues).analyze();
    }

    private Signal[] findIntersectionSignals() {
        Signal[] buySignals = findBuySignals();
        Signal[] sellSignals = findSellSignals();
        return mergeSignals(buySignals, sellSignals);
    }

    private Signal[] findBuySignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, bullishSignalLine).analyze())
                .map(signal -> removeFalsePositiveSignal(signal, SELL))
                .toArray(Signal[]::new);
    }

    private Signal[] findSellSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, bearerSignalLine).analyze())
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
        return indicatorValue.compareTo(bullishSignalLine) <= 0
                && indicatorValue.compareTo(bearerSignalLine) >= 0;
    }

    private boolean isBullishZone(BigDecimal indicatorValue) {
        return indicatorValue.compareTo(bullishSignalLine) > 0;
    }

    private boolean isBearerZone(BigDecimal indicatorValue) {
        return indicatorValue.compareTo(bearerSignalLine) < 0;
    }

    private void buildCMFAnalyzerResult(Signal[] mergedSignals, Trend[] trends) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new CMFAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx], trends[idx]))
                .toArray(CMFAnalyzerResult[]::new);
    }

}
