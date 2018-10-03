package pro.crypto.analyzer.eri;

import pro.crypto.helper.DynamicLineCrossFinder;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.eri.ERIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Strength.NORMAL;
import static pro.crypto.model.Strength.STRONG;

public class ERIAnalyzer implements Analyzer<ERIAnalyzerResult> {

    private final Tick[] originalData;
    private final ERIResult[] indicatorResults;

    private ERIAnalyzerResult[] result;

    public ERIAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (ERIResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        SignalStrength[] mainSignals = findMainSignals();
        SignalStrength[] crossZeroSignals = findCrossLinesSignals();
        SignalStrength[] mergedSignals = mergeSignalsStrength(mainSignals, crossZeroSignals);
        buildERIAnalyzerResult(mergedSignals);
    }

    @Override
    public ERIAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private SignalStrength[] findMainSignals() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::findMainSignal)
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength findMainSignal(int currentIndex) {
        return isPossibleDefineSignal(currentIndex)
                ? defineMainSignal(currentIndex)
                : null;
    }

    private boolean isPossibleDefineSignal(int currentIndex) {
        return currentIndex > 0
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex - 1].getSignalLineValue())
                && nonNull(indicatorResults[currentIndex].getMovingAverageValue())
                && nonNull(indicatorResults[currentIndex].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getSignalLineValue());
    }

    private SignalStrength defineMainSignal(int currentIndex) {
        if (isBuySignal(currentIndex)) {
            return new SignalStrength(BUY, STRONG);
        }

        if (isSellSignal(currentIndex)) {
            return new SignalStrength(SELL, STRONG);
        }

        return null;
    }

    private boolean isBuySignal(int currentIndex) {
        return isPriceHigherMovingAverage(currentIndex)
                && isIndicatorIncrease(currentIndex)
                && isSignalLineIncrease(currentIndex)
                && isIndicatorHigherSignalLine(currentIndex);
    }

    private boolean isPriceHigherMovingAverage(int currentIndex) {
        return originalData[currentIndex].getClose().compareTo(indicatorResults[currentIndex].getMovingAverageValue()) > 0;
    }

    private boolean isIndicatorIncrease(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getIndicatorValue()) > 0;
    }

    private boolean isSignalLineIncrease(int currentIndex) {
        return indicatorResults[currentIndex].getSignalLineValue().compareTo(indicatorResults[currentIndex - 1].getSignalLineValue()) > 0;
    }

    private boolean isIndicatorHigherSignalLine(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex].getSignalLineValue()) > 0;
    }

    private boolean isSellSignal(int currentIndex) {
        return isPriceLowerMovingAverage(currentIndex)
                && isIndicatorDecrease(currentIndex)
                && isSignalLineDecrease(currentIndex)
                && isIndicatorLowerSignalLine(currentIndex);
    }

    private boolean isPriceLowerMovingAverage(int currentIndex) {
        return originalData[currentIndex].getClose().compareTo(indicatorResults[currentIndex].getMovingAverageValue()) < 0;
    }

    private boolean isIndicatorDecrease(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getIndicatorValue()) < 0;
    }

    private boolean isSignalLineDecrease(int currentIndex) {
        return indicatorResults[currentIndex].getSignalLineValue().compareTo(indicatorResults[currentIndex - 1].getSignalLineValue()) < 0;
    }

    private boolean isIndicatorLowerSignalLine(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex].getSignalLineValue()) < 0;
    }

    private SignalStrength[] findCrossLinesSignals() {
        return Stream.of(new DynamicLineCrossFinder(extractSmoothedLine(), IndicatorResultExtractor.extractSignalLineValues(indicatorResults)).find())
                .map(signal -> toSignalStrength(signal, NORMAL))
                .toArray(SignalStrength[]::new);
    }

    private BigDecimal[] extractSmoothedLine() {
        return Stream.of(indicatorResults)
                .map(ERIResult::getSmoothedLineValue)
                .toArray(BigDecimal[]::new);
    }

    private void buildERIAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new ERIAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx]))
                .toArray(ERIAnalyzerResult[]::new);
    }

}
