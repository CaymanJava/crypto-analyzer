package pro.crypto.analyzer.rma;

import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceVolumeExtractor;
import pro.crypto.indicator.rma.RMAResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Strength.NORMAL;
import static pro.crypto.model.Strength.STRONG;
import static pro.crypto.model.Strength.WEAK;
import static pro.crypto.model.Trend.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RMAAnalyzer implements Analyzer<RMAAnalyzerResult> {

    private final Tick[] originalData;
    private final RMAResult[] indicatorResults;

    private Trend[] trends;
    private RMAAnalyzerResult[] result;

    public RMAAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (RMAResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        defineTrends();
        SignalStrength[] crossLinesSignals = findCrossLinesSignals();
        SignalStrength[] trendReverseSignals = findTrendReverseSignals();
        SignalStrength[] mergedSignals = mergeSignalsStrength(crossLinesSignals, trendReverseSignals);
        buildRMAAnalyzerResult(mergedSignals);
    }

    @Override
    public RMAAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void defineTrends() {
        trends = IntStream.range(0, indicatorResults.length)
                .mapToObj(this::tryDefineTrend)
                .toArray(Trend[]::new);
    }

    private Trend tryDefineTrend(int currentIndex) {
        return isAllLinesPresent(currentIndex)
                ? defineTrend(currentIndex)
                : UNDEFINED;
    }

    private Trend defineTrend(int currentIndex) {
        if (isUptrend(currentIndex)) {
            return UPTREND;
        }

        if (isDowntrend(currentIndex)) {
            return DOWNTREND;
        }

        return CONSOLIDATION;
    }

    private boolean isUptrend(int currentIndex) {
        return isFirstLineHigherSecondOne(RMAResult::getFirstMaValue, RMAResult::getSecondMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getSecondMaValue, RMAResult::getThirdMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getThirdMaValue, RMAResult::getFourthMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getFourthMaValue, RMAResult::getFifthMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getFifthMaValue, RMAResult::getSixthMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getSixthMaValue, RMAResult::getSeventhMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getSeventhMaValue, RMAResult::getEighthMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getEighthMaValue, RMAResult::getNinthMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getNinthMaValue, RMAResult::getTenthMaValue, currentIndex);
    }

    private boolean isDowntrend(int currentIndex) {
        return isFirstLineHigherSecondOne(RMAResult::getTenthMaValue, RMAResult::getNinthMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getNinthMaValue, RMAResult::getEighthMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getEighthMaValue, RMAResult::getSeventhMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getSeventhMaValue, RMAResult::getSixthMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getSixthMaValue, RMAResult::getFifthMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getFifthMaValue, RMAResult::getFourthMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getFourthMaValue, RMAResult::getThirdMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getThirdMaValue, RMAResult::getSecondMaValue, currentIndex)
                && isFirstLineHigherSecondOne(RMAResult::getSecondMaValue, RMAResult::getFirstMaValue, currentIndex);
    }

    private boolean isFirstLineHigherSecondOne(Function<RMAResult, BigDecimal> firstLineFunction,
                                               Function<RMAResult, BigDecimal> secondLineFunction,
                                               int currentIndex) {
        return firstLineFunction.apply(indicatorResults[currentIndex]).compareTo(secondLineFunction.apply(indicatorResults[currentIndex])) > 0;
    }

    private SignalStrength[] findCrossLinesSignals() {
        BigDecimal[] closePrices = PriceVolumeExtractor.extractPrices(originalData, CLOSE);
        SignalStrength[] higherLineCrossSignals = findHigherLinesCrossSignals(closePrices);
        SignalStrength[] lowerLineCrossSignals = findLowerLinesCrossSignals(closePrices);
        return mergeSignalsStrength(higherLineCrossSignals, lowerLineCrossSignals);
    }

    private SignalStrength[] findHigherLinesCrossSignals(BigDecimal[] closePrices) {
        BigDecimal[] higherValues = extractMaxValues();
        Signal[] signals = new DynamicLineCrossAnalyzer(closePrices, higherValues).analyze();
        return IntStream.range(0, indicatorResults.length)
                .map(idx -> confirmCrossHigherLineSignal(signals, idx))
                .mapToObj(idx -> toSignalStrength(signals[idx], defineCrossHigherLineStrength(signals[idx], idx)))
                .toArray(SignalStrength[]::new);
    }

    private BigDecimal[] extractMaxValues() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::extractMaxValue)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal extractMaxValue(int currentIndex) {
        return isAllLinesPresent(currentIndex)
                ? MathHelper.max(indicatorResults[currentIndex].getAllValues())
                : null;
    }

    private int confirmCrossHigherLineSignal(Signal[] signals, int currentIndex) {
        if (signals[currentIndex] == SELL && trends[currentIndex] == UPTREND) {
            signals[currentIndex] = null;
        }
        return currentIndex;
    }

    private Strength defineCrossHigherLineStrength(Signal signal, int currentIndex) {
        if (isNull(signal)) {
            return null;
        }

        if (currentIndex > 0 && signal == SELL && trends[currentIndex - 1] == UPTREND && trends[currentIndex] != UPTREND) {
            return NORMAL;
        }

        return WEAK;
    }

    private SignalStrength[] findLowerLinesCrossSignals(BigDecimal[] closePrices) {
        BigDecimal[] lowerValues = extractMinValues();
        Signal[] signals = new DynamicLineCrossAnalyzer(closePrices, lowerValues).analyze();
        return IntStream.range(0, indicatorResults.length)
                .map(idx -> confirmCrossLowerLineSignal(signals, idx))
                .mapToObj(idx -> toSignalStrength(signals[idx], defineCrossLowerLineStrength(signals[idx], idx)))
                .toArray(SignalStrength[]::new);
    }

    private BigDecimal[] extractMinValues() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::extractMinValue)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal extractMinValue(int currentIndex) {
        return isAllLinesPresent(currentIndex)
                ? MathHelper.min(indicatorResults[currentIndex].getAllValues())
                : null;
    }

    private int confirmCrossLowerLineSignal(Signal[] signals, int currentIndex) {
        if (signals[currentIndex] == BUY && trends[currentIndex] == DOWNTREND) {
            signals[currentIndex] = null;
        }
        return currentIndex;
    }

    private Strength defineCrossLowerLineStrength(Signal signal, int currentIndex) {
        if (isNull(signal)) {
            return null;
        }

        if (currentIndex > 0 && signal == BUY && trends[currentIndex - 1] == DOWNTREND && trends[currentIndex] != DOWNTREND) {
            return NORMAL;
        }

        return WEAK;
    }

    private boolean isAllLinesPresent(int currentIndex) {
        return nonNull(indicatorResults[currentIndex].getFirstMaValue())
                && nonNull(indicatorResults[currentIndex].getSecondMaValue())
                && nonNull(indicatorResults[currentIndex].getThirdMaValue())
                && nonNull(indicatorResults[currentIndex].getFourthMaValue())
                && nonNull(indicatorResults[currentIndex].getFifthMaValue())
                && nonNull(indicatorResults[currentIndex].getSixthMaValue())
                && nonNull(indicatorResults[currentIndex].getSeventhMaValue())
                && nonNull(indicatorResults[currentIndex].getEighthMaValue())
                && nonNull(indicatorResults[currentIndex].getNinthMaValue())
                && nonNull(indicatorResults[currentIndex].getTenthMaValue());
    }

    private SignalStrength[] findTrendReverseSignals() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::findTrendReverseSignal)
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength findTrendReverseSignal(int currentIndex) {
        return isPossibleDefineTrendReverseSignal(currentIndex)
                ? defineTrendReverseSignal(currentIndex)
                : null;
    }

    private boolean isPossibleDefineTrendReverseSignal(int currentIndex) {
        return currentIndex > 0
                && trends[currentIndex - 1] != UNDEFINED
                && trends[currentIndex] != UNDEFINED;
    }

    private SignalStrength defineTrendReverseSignal(int currentIndex) {
        if (isBuySignal(currentIndex)) {
            return new SignalStrength(BUY, STRONG);
        }

        if (isSellSignal(currentIndex)) {
            return new SignalStrength(SELL, STRONG);
        }

        return null;
    }

    private boolean isBuySignal(int currentIndex) {
        return trends[currentIndex] == UPTREND && trends[currentIndex - 1] != UPTREND;
    }

    private boolean isSellSignal(int currentIndex) {
        return trends[currentIndex] == DOWNTREND && trends[currentIndex - 1] != DOWNTREND;
    }

    private void buildRMAAnalyzerResult(SignalStrength[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new RMAAnalyzerResult(indicatorResults[idx].getTime(), signals[idx], trends[idx]))
                .toArray(RMAAnalyzerResult[]::new);
    }

}
