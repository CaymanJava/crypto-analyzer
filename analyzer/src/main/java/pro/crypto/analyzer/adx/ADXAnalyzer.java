package pro.crypto.analyzer.adx;

import pro.crypto.indicator.adx.ADXResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.TrendStrength.*;

public class ADXAnalyzer implements Analyzer<ADXAnalyzerResult>{

    private final Tick[] originalData;
    private final ADXResult[] indicatorResults;

    private ADXAnalyzerResult[] result;

    public ADXAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (ADXResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        TrendStrength[] trendStrengths = defineTrendStrengths();
        Signal[] signals = recognizeSignals();
        buildADXAnalyzerResults(trendStrengths, signals);
    }

    @Override
    public ADXAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private TrendStrength[] defineTrendStrengths() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::defineTrendStrength)
                .toArray(TrendStrength[]::new);
    }

    private TrendStrength defineTrendStrength(int currentIndex) {
        if (isNull(indicatorResults[currentIndex].getAverageDirectionalIndex())) {
            return NORMAL;
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
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::tryRecognizeSignal)
                .toArray(Signal[]::new);
    }

    private Signal tryRecognizeSignal(int currentIndex) {
        return possibleRecognizeSignal(currentIndex)
                ? recognizeSignal(currentIndex)
                : NEUTRAL;
    }

    private boolean possibleRecognizeSignal(int currentIndex) {
        return currentIndex > 0
                && nonNull(indicatorResults[currentIndex].getPositiveDirectionalIndicator())
                && nonNull(indicatorResults[currentIndex].getNegativeDirectionalIndicator())
                && nonNull(indicatorResults[currentIndex - 1].getPositiveDirectionalIndicator())
                && nonNull(indicatorResults[currentIndex - 1].getNegativeDirectionalIndicator());
    }

    private Signal recognizeSignal(int currentIndex) {
        return isIntersection(currentIndex)
                ? recognizeBuyOrSellSignal(currentIndex)
                : NEUTRAL;
    }

    private boolean isIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getPositiveDirectionalIndicator()
                .compareTo(indicatorResults[currentIndex - 1].getNegativeDirectionalIndicator()) !=
                        indicatorResults[currentIndex].getPositiveDirectionalIndicator()
                                .compareTo(indicatorResults[currentIndex].getNegativeDirectionalIndicator());
    }

    private Signal recognizeBuyOrSellSignal(int currentIndex) {
        return indicatorResults[currentIndex].getPositiveDirectionalIndicator()
                .compareTo(indicatorResults[currentIndex].getNegativeDirectionalIndicator()) > 0
                ? BUY
                : SELL;
    }

    private void buildADXAnalyzerResults(TrendStrength[] trendStrengths, Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> buildADXAnalyzerResult(trendStrengths[idx], signals[idx], idx))
                .toArray(ADXAnalyzerResult[]::new);
    }

    private AnalyzerResult buildADXAnalyzerResult(TrendStrength trendStrength, Signal signal, int currentIndex) {
        return ADXAnalyzerResult.builder()
                .time(originalData[currentIndex].getTickTime())
                .signal(signal)
                .indicatorValue(indicatorResults[currentIndex].getAverageDirectionalIndex())
                .closePrice(originalData[currentIndex].getClose())
                .entryPoint(definePotentialEntryPoint(signal, currentIndex))
                .trendStrength(trendStrength)
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
