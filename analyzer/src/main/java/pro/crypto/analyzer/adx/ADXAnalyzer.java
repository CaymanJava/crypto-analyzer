package pro.crypto.analyzer.adx;

import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.indicator.adx.ADXResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.Signal;
import pro.crypto.model.analyzer.Strength;
import pro.crypto.model.analyzer.Trend;
import pro.crypto.model.analyzer.TrendStrength;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.analyzer.Strength.NORMAL;
import static pro.crypto.model.analyzer.Strength.STRONG;
import static pro.crypto.model.analyzer.Strength.UNDEFINED;
import static pro.crypto.model.analyzer.Strength.WEAK;

public class ADXAnalyzer implements Analyzer<ADXAnalyzerResult> {

    private final Tick[] originalData;
    private final ADXResult[] indicatorResults;
    private final BigDecimal weakTrendLine;
    private final BigDecimal strongTrendLine;

    private ADXAnalyzerResult[] result;

    public ADXAnalyzer(AnalyzerRequest analyzerRequest) {
        ADXAnalyzerRequest request = (ADXAnalyzerRequest) analyzerRequest;
        this.originalData = request.getOriginalData();
        this.indicatorResults = (ADXResult[]) request.getIndicatorResults();
        this.weakTrendLine = extractWeakTrendLine(request);
        this.strongTrendLine = extractStrongTrendLine(request);
    }

    @Override
    public void analyze() {
        Strength[] strengths = defineTrendStrengths();
        Signal[] signals = recognizeSignals();
        buildADXAnalyzerResults(strengths, signals);
    }

    @Override
    public ADXAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractWeakTrendLine(ADXAnalyzerRequest request) {
        return ofNullable(request.getWeakTrendLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(20));
    }

    private BigDecimal extractStrongTrendLine(ADXAnalyzerRequest request) {
        return ofNullable(request.getStrongTrendLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(40));
    }

    private Strength[] defineTrendStrengths() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::defineTrendStrength)
                .toArray(Strength[]::new);
    }

    private Strength defineTrendStrength(int currentIndex) {
        if (isNull(indicatorResults[currentIndex].getAverageDirectionalIndex())) {
            return UNDEFINED;
        }

        if (indicatorResults[currentIndex].getAverageDirectionalIndex().compareTo(weakTrendLine) < 0) {
            return WEAK;
        }

        if (indicatorResults[currentIndex].getAverageDirectionalIndex().compareTo(strongTrendLine) >= 0) {
            return STRONG;
        }

        return NORMAL;
    }

    private Signal[] recognizeSignals() {
        return new DynamicLineCrossAnalyzer(
                extractIndex(ADXResult::getPositiveDirectionalIndicator),
                extractIndex(ADXResult::getNegativeDirectionalIndicator))
                .analyze();
    }

    private BigDecimal[] extractIndex(Function<ADXResult, BigDecimal> directionIndicatorFunction) {
        return Stream.of(indicatorResults)
                .map(directionIndicatorFunction)
                .toArray(BigDecimal[]::new);
    }

    private void buildADXAnalyzerResults(Strength[] strengths, Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> buildADXAnalyzerResult(strengths[idx], signals[idx], idx))
                .toArray(ADXAnalyzerResult[]::new);
    }

    private ADXAnalyzerResult buildADXAnalyzerResult(Strength strength, Signal signal, int currentIndex) {
        return ADXAnalyzerResult.builder()
                .time(originalData[currentIndex].getTickTime())
                .signal(signal)
                .trendStrength(new TrendStrength(Trend.UNDEFINED, strength))
                .entryPoint(definePotentialEntryPoint(signal, currentIndex))
                .build();
    }

    private BigDecimal definePotentialEntryPoint(Signal signal, int currentIndex) {
        switch (signal) {
            case BUY:
                return originalData[currentIndex].getHigh();
            case SELL:
                return originalData[currentIndex].getLow();
            default:
                return null;
        }
    }

}
