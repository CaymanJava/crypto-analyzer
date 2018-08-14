package pro.crypto.analyzer.adx;

import pro.crypto.indicator.adx.ADXResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.*;

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
        return isPossibleRecognizeSignal(currentIndex)
                ? recognizeSignal(currentIndex)
                : NEUTRAL;
    }

    private boolean isPossibleRecognizeSignal(int currentIndex) {
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

    private void buildADXAnalyzerResults(Strength[] strengths, Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> buildADXAnalyzerResult(strengths[idx], signals[idx], idx))
                .toArray(ADXAnalyzerResult[]::new);
    }

    private AnalyzerResult buildADXAnalyzerResult(Strength strength, Signal signal, int currentIndex) {
        return ADXAnalyzerResult.builder()
                .time(originalData[currentIndex].getTickTime())
                .signal(signal)
                .indicatorValue(indicatorResults[currentIndex].getAverageDirectionalIndex())
                .closePrice(originalData[currentIndex].getClose())
                .entryPoint(definePotentialEntryPoint(signal, currentIndex))
                .strength(strength)
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
