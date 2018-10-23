package pro.crypto.analyzer.ic;

import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.helper.PriceVolumeExtractor;
import pro.crypto.indicator.ic.ICResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Strength.*;
import static pro.crypto.model.Trend.*;
import static pro.crypto.model.Trend.UNDEFINED;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ICAnalyzer implements Analyzer<ICAnalyzerResult> {

    private final static TrendStrength STRONG_UPTREND = new TrendStrength(UPTREND, STRONG);
    private final static TrendStrength STRONG_DOWNTREND = new TrendStrength(DOWNTREND, STRONG);

    private final Tick[] originalData;
    private final ICResult[] indicatorResults;

    private TrendStrength[] trendStrengths;
    private ICAnalyzerResult[] result;

    public ICAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (ICResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        defineTrendStrengths();
        SignalStrength[] tenkanKijunCrossSignals = findTenkanKijunCrossSignals();
        SignalStrength[] priceKijunCrossSignals = findPriceKijunCrossSignals();
        SignalStrength[] priceCloudCrossSignals = findPriceCloudCrossSignals();
        SignalStrength[] mergedSignals = mergeSignalsStrength(tenkanKijunCrossSignals, priceKijunCrossSignals, priceCloudCrossSignals);
        buildICAnalyzerResult(mergedSignals);
    }

    @Override
    public ICAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void defineTrendStrengths() {
        trendStrengths = IntStream.range(0, indicatorResults.length)
                .mapToObj(this::tryDefineTrendStrength)
                .toArray(TrendStrength[]::new);
    }

    private TrendStrength tryDefineTrendStrength(int currentIndex) {
        return isCloudExist(currentIndex)
                ? defineTrendStrength(currentIndex)
                : new TrendStrength(UNDEFINED, Strength.UNDEFINED);
    }

    private TrendStrength defineTrendStrength(int currentIndex) {
        if (isUptrend(currentIndex)) {
            return new TrendStrength(UPTREND, defineUptrendStrength(currentIndex));
        }

        if (isDowntrend(currentIndex)) {
            return new TrendStrength(DOWNTREND, defineDowntrendStrength(currentIndex));
        }

        return new TrendStrength(CONSOLIDATION, NORMAL);
    }

    private boolean isUptrend(int currentIndex) {
        return isSpanALineUnderPrice(currentIndex) && isSpanBLineUnderPrice(currentIndex);
    }

    private boolean isSpanALineUnderPrice(int currentIndex) {
        return indicatorResults[currentIndex].getLeadingSpanAValue().compareTo(originalData[currentIndex].getClose()) < 0;
    }

    private boolean isSpanBLineUnderPrice(int currentIndex) {
        return indicatorResults[currentIndex].getLeadingSpanBValue().compareTo(originalData[currentIndex].getClose()) < 0;
    }

    private boolean isSpanALineAbovePrice(int currentIndex) {
        return indicatorResults[currentIndex].getLeadingSpanAValue().compareTo(originalData[currentIndex].getClose()) > 0;
    }

    private boolean isSpanBLineAbovePrice(int currentIndex) {
        return indicatorResults[currentIndex].getLeadingSpanBValue().compareTo(originalData[currentIndex].getClose()) > 0;
    }

    private Strength defineUptrendStrength(int currentIndex) {
        return isSpanALineAboveSpanBLine(indicatorResults[currentIndex])
                ? STRONG
                : WEAK;
    }

    private boolean isSpanALineAboveSpanBLine(ICResult indicatorResult) {
        return indicatorResult.getLeadingSpanAValue().compareTo(indicatorResult.getLeadingSpanBValue()) > 0;
    }

    private boolean isDowntrend(int currentIndex) {
        return isSpanALineAbovePrice(currentIndex) && isSpanBLineAbovePrice(currentIndex);
    }

    private Strength defineDowntrendStrength(int currentIndex) {
        return isSpanALineUnderSpanBLine(indicatorResults[currentIndex])
                ? STRONG
                : WEAK;
    }

    private boolean isSpanALineUnderSpanBLine(ICResult indicatorResult) {
        return indicatorResult.getLeadingSpanAValue().compareTo(indicatorResult.getLeadingSpanBValue()) < 0;
    }

    private SignalStrength[] findTenkanKijunCrossSignals() {
        Signal[] signals = new DynamicLineCrossAnalyzer(extractLine(ICResult::getConversionLineValue), extractLine(ICResult::getBaseLineValue)).analyze();
        return IntStream.range(0, signals.length)
                .mapToObj(idx -> toSignalStrength(signals[idx], tryDefineSignalStrength(signals[idx], idx)))
                .toArray(SignalStrength[]::new);
    }

    private Strength tryDefineSignalStrength(Signal signal, int currentIndex) {
        return nonNull(signal)
                ? defineSignalStrength(signal, currentIndex)
                : null;
    }

    private Strength defineSignalStrength(Signal signal, int currentIndex) {
        if (signal == BUY && isStrongUptrend(currentIndex)) {
            return STRONG;
        }

        if (signal == SELL && isStrongDowntrend(currentIndex)) {
            return STRONG;
        }

        return WEAK;
    }

    private boolean isStrongDowntrend(int currentIndex) {
        return nonNull(trendStrengths[currentIndex]) && trendStrengths[currentIndex].equals(STRONG_DOWNTREND);
    }

    private SignalStrength[] findPriceKijunCrossSignals() {
        Signal[] signals = new DynamicLineCrossAnalyzer(
                PriceVolumeExtractor.extractPrices(originalData, CLOSE), extractLine(ICResult::getBaseLineValue)).analyze();
        return IntStream.range(0, signals.length)
                .mapToObj(idx -> toSignalStrength(signals[idx], idx))
                .toArray(SignalStrength[]::new);
    }

    private BigDecimal[] extractLine(Function<ICResult, BigDecimal> extractLineFunction) {
        return Stream.of(indicatorResults)
                .map(extractLineFunction)
                .toArray(BigDecimal[]::new);
    }

    private SignalStrength toSignalStrength(Signal signal, int currentIndex) {
        return isBuySignal(signal) && isStrongUptrend(currentIndex)
                ? new SignalStrength(BUY, STRONG)
                : null;
    }

    private boolean isBuySignal(Signal signal) {
        return nonNull(signal) && signal == BUY;
    }

    private boolean isStrongUptrend(int currentIndex) {
        return nonNull(trendStrengths[currentIndex]) && trendStrengths[currentIndex].equals(STRONG_UPTREND);
    }

    private SignalStrength[] findPriceCloudCrossSignals() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::tryDefinePriceCloudCrossSignal)
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength tryDefinePriceCloudCrossSignal(int currentIndex) {
        return isPossibleDefinePriceCloudCrossSignal(currentIndex) && isPriceLeftCloud(currentIndex)
                ? definePriceCloudCrossSignal(currentIndex)
                : null;
    }

    private boolean isPossibleDefinePriceCloudCrossSignal(int currentIndex) {
        return currentIndex > 0 && isCloudExist(currentIndex - 1) && isCloudExist(currentIndex);
    }

    private boolean isCloudExist(int currentIndex) {
        return nonNull(indicatorResults[currentIndex].getLeadingSpanAValue())
                && nonNull(indicatorResults[currentIndex].getLeadingSpanBValue());
    }

    private SignalStrength definePriceCloudCrossSignal(int currentIndex) {
        if (isSpanALineUnderPrice(currentIndex) && isSpanBLineUnderPrice(currentIndex)) {
            return new SignalStrength(BUY, STRONG);
        }

        if (isSpanALineAbovePrice(currentIndex) && isSpanBLineAbovePrice(currentIndex)) {
            return new SignalStrength(SELL, STRONG);
        }

        return null;
    }

    private boolean isPriceLeftCloud(int currentIndex) {
        return isPriceInCloud(currentIndex - 1) && !isPriceInCloud(currentIndex);
    }

    private boolean isPriceInCloud(int currentIndex) {
        return (isSpanALineAbovePrice(currentIndex) && isSpanBLineUnderPrice(currentIndex))
                || (isSpanBLineAbovePrice(currentIndex) && isSpanALineUnderPrice(currentIndex));

    }

    private void buildICAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new ICAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx], trendStrengths[idx]))
                .toArray(ICAnalyzerResult[]::new);
    }

}
