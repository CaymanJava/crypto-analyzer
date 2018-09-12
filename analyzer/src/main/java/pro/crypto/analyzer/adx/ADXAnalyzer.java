package pro.crypto.analyzer.adx;

import pro.crypto.analyzer.helper.DynamicLineCrossFinder;
import pro.crypto.indicator.adx.ADXResult;
import pro.crypto.model.*;
import pro.crypto.model.result.AnalyzerResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.Strength.*;

public class ADXAnalyzer implements Analyzer<ADXAnalyzerResult> {

    private final Tick[] originalData;
    private final ADXResult[] indicatorResults;

    private ADXAnalyzerResult[] result;

    public ADXAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (ADXResult[]) request.getIndicatorResults();
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

    private Strength[] defineTrendStrengths() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::defineTrendStrength)
                .toArray(Strength[]::new);
    }

    private Strength defineTrendStrength(int currentIndex) {
        if (isNull(indicatorResults[currentIndex].getAverageDirectionalIndex())) {
            return UNDEFINED;
        }
        if (indicatorResults[currentIndex].getAverageDirectionalIndex().compareTo(new BigDecimal(20)) < 0) {
            return WEAK;
        }
        if (indicatorResults[currentIndex].getAverageDirectionalIndex().compareTo(new BigDecimal(50)) >= 0) {
            return STRONG;
        }
        return NORMAL;
    }

    private Signal[] recognizeSignals() {
        return new DynamicLineCrossFinder(extractIndex(
                ADXResult::getPositiveDirectionalIndicator),
                extractIndex(ADXResult::getNegativeDirectionalIndicator)).find();
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

    private AnalyzerResult buildADXAnalyzerResult(Strength strength, Signal signal, int currentIndex) {
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
