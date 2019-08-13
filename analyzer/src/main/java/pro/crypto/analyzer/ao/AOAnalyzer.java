package pro.crypto.analyzer.ao;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.helper.SignalMerger;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.ao.AOResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.Signal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.analyzer.Signal.BUY;
import static pro.crypto.model.analyzer.Signal.SELL;

public class AOAnalyzer implements Analyzer<AOAnalyzerResult> {

    private final static int POSSIBLE_INDEX_AMOUNT_TO_TWO_PEAKS = 6;

    private final AOResult[] indicatorResults;

    private AOAnalyzerResult[] result;

    public AOAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (AOResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] saucerSignals = findSaucerSignals();
        Signal[] crossZeroSignals = findCrossZeroSignals();
        Signal[] twoPeaksSignals = findTwoPeaksSignals();
        Signal[] signals = SignalArrayMerger.mergeSignals(saucerSignals, crossZeroSignals, twoPeaksSignals);
        buildAOAnalyzerResult(signals);
    }

    @Override
    public AOAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findSaucerSignals() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::findSaucerSignal)
                .toArray(Signal[]::new);
    }

    private Signal findSaucerSignal(int currentIndex) {
        return isPossibleToDefineThreeValuesSignal(currentIndex) && threeIndicatorValuesInSamePlane(currentIndex)
                ? defineSaucerSignal(currentIndex)
                : null;
    }

    private boolean threeIndicatorValuesInSamePlane(int currentIndex) {
        return (indicatorResults[currentIndex - 2].getIndicatorValue().compareTo(ZERO)
                == indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(ZERO))
                && (indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(ZERO)
                == indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO));
    }

    private Signal defineSaucerSignal(int currentIndex) {
        return threeIndicatorValuesAboveZero(currentIndex)
                ? tryDefineBuySignal(currentIndex)
                : tryDefineSellSignal(currentIndex);
    }

    private boolean threeIndicatorValuesAboveZero(int currentIndex) {
        return indicatorResults[currentIndex - 2].getIndicatorValue().compareTo(ZERO) > 0
                && indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(ZERO) > 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO) > 0;
    }

    private Signal tryDefineBuySignal(int currentIndex) {
        return isRedBar(currentIndex - 2) && isRedBar(currentIndex - 1) && isGreenBar(currentIndex)
                ? BUY : null;
    }

    private Signal tryDefineSellSignal(int currentIndex) {
        return isGreenBar(currentIndex - 2) && isGreenBar(currentIndex - 1) && isRedBar(currentIndex)
                ? SELL : null;
    }

    private Signal[] findCrossZeroSignals() {
        return new StaticLineCrossAnalyzer(IndicatorResultExtractor.extractIndicatorValues(indicatorResults), ZERO).analyze();
    }

    private Signal[] findTwoPeaksSignals() {
        return mergeTwoPeaksSignals(defineTwoPeaksBuySignals(), defineTwoPeaksSellSignals());
    }

    private Signal[] defineTwoPeaksBuySignals() {
        Signal[] buySignals = new Signal[indicatorResults.length];
        findIndicatorNegativeSegments().stream()
                .filter(segment -> segment.size() >= POSSIBLE_INDEX_AMOUNT_TO_TWO_PEAKS)
                .map(this::findTwoPeaksBuySignalsIndexes)
                .filter(signalIndexes -> signalIndexes.length > 0)
                .flatMap(Stream::of)
                .forEach(signalIndex -> buySignals[signalIndex] = BUY);
        return buySignals;
    }

    private List<List<Integer>> findIndicatorNegativeSegments() {
        List<List<Integer>> negativeSegments = new ArrayList<>();
        int[] negativeIndexes = defineNegativeIndexes();
        if (negativeIndexes.length >= POSSIBLE_INDEX_AMOUNT_TO_TWO_PEAKS) {
            findIndicatorSegments(negativeSegments, negativeIndexes);
        }
        return negativeSegments;
    }

    private boolean indexesConsistent(int[] negativeIndexes, int currentIndex) {
        return negativeIndexes[currentIndex - 1] + 1 == negativeIndexes[currentIndex];
    }

    private int[] defineNegativeIndexes() {
        return IntStream.range(0, indicatorResults.length)
                .filter(this::isIndicatorNegative)
                .toArray();
    }

    private boolean isIndicatorNegative(int currentIndex) {
        return nonNull(indicatorResults[currentIndex].getIndicatorValue())
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO) < 0;
    }

    private Integer[] findTwoPeaksBuySignalsIndexes(List<Integer> segment) {
        int[] peaksIndexes = findNegativePeaksIndexes(segment);
        return peaksIndexes.length >= 2
                ? findPeaksBuySignalIndexes(peaksIndexes)
                : new Integer[0];
    }

    private Integer[] findPeaksBuySignalIndexes(int[] peaksIndexes) {
        return IntStream.range(1, peaksIndexes.length)
                .filter(idx -> isSecondPeakMoreThanPrevious(peaksIndexes, idx))
                .mapToObj(idx -> peaksIndexes[idx] + 1)
                .toArray(Integer[]::new);
    }

    private boolean isSecondPeakMoreThanPrevious(int[] peaksIndexes, int currentIndex) {
        return indicatorResults[peaksIndexes[currentIndex]].getIndicatorValue()
                .compareTo(indicatorResults[peaksIndexes[currentIndex - 1]].getIndicatorValue()) > 0;
    }

    private int[] findNegativePeaksIndexes(List<Integer> segment) {
        return IntStream.range(1, segment.size() - 1)
                .filter(idx -> isNegativePeak(segment.get(idx)))
                .map(segment::get)
                .toArray();
    }

    private boolean isNegativePeak(int index) {
        return isPossibleToDefineThreeValuesSignal(index)
                && isRedBar(index)
                && isGreenBar(index + 1);
    }

    private boolean isPossibleToDefineThreeValuesSignal(int currentIndex) {
        return currentIndex > 1
                && nonNull(indicatorResults[currentIndex - 2].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex - 2].getIncreased())
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex - 1].getIncreased())
                && nonNull(indicatorResults[currentIndex].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getIncreased());
    }

    private Signal[] defineTwoPeaksSellSignals() {
        Signal[] sellSignals = new Signal[indicatorResults.length];
        findIndicatorPositiveSegments().stream()
                .filter(segment -> segment.size() >= POSSIBLE_INDEX_AMOUNT_TO_TWO_PEAKS)
                .map(this::findTwoPeaksSellSignalsIndexes)
                .filter(signalIndexes -> signalIndexes.length > 0)
                .flatMap(Stream::of)
                .forEach(signalIndex -> sellSignals[signalIndex] = SELL);
        return sellSignals;
    }

    private List<List<Integer>> findIndicatorPositiveSegments() {
        List<List<Integer>> positiveSegments = new ArrayList<>();
        int[] positiveIndexes = definePositiveIndexes();
        if (positiveIndexes.length >= POSSIBLE_INDEX_AMOUNT_TO_TWO_PEAKS) {
            findIndicatorSegments(positiveSegments, positiveIndexes);
        }
        return positiveSegments;
    }

    private int[] definePositiveIndexes() {
        return IntStream.range(0, indicatorResults.length)
                .filter(this::isIndicatorPositive)
                .toArray();
    }

    private boolean isIndicatorPositive(int currentIndex) {
        return nonNull(indicatorResults[currentIndex].getIndicatorValue())
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO) > 0;
    }

    private void findIndicatorSegments(List<List<Integer>> segments, int[] indexes) {
        List<Integer> segment = new ArrayList<>();
        IntStream.range(1, indexes.length)
                .forEach(idx -> {
                    if (indexesConsistent(indexes, idx)) {
                        if (segment.size() == 0) {
                            segment.add(indexes[idx - 1]);
                        }
                        segment.add(indexes[idx]);
                    } else if (segment.size() != 0) {
                        segments.add(new ArrayList<>(segment));
                        segment.clear();
                    }
                });
        if (segment.size() != 0) {
            segments.add(new ArrayList<>(segment));
        }
    }

    private Integer[] findTwoPeaksSellSignalsIndexes(List<Integer> segment) {
        int[] peaksIndexes = findPositivePeaksIndexes(segment);
        return peaksIndexes.length >= 2
                ? findPeaksSellSignalIndexes(peaksIndexes)
                : new Integer[0];
    }

    private Integer[] findPeaksSellSignalIndexes(int[] peaksIndexes) {
        return IntStream.range(1, peaksIndexes.length)
                .filter(idx -> isSecondPeakLessThanPrevious(peaksIndexes, idx))
                .mapToObj(idx -> peaksIndexes[idx] + 1)
                .toArray(Integer[]::new);
    }

    private boolean isSecondPeakLessThanPrevious(int[] peaksIndexes, int currentIndex) {
        return indicatorResults[peaksIndexes[currentIndex]].getIndicatorValue()
                .compareTo(indicatorResults[peaksIndexes[currentIndex - 1]].getIndicatorValue()) < 0;
    }

    private int[] findPositivePeaksIndexes(List<Integer> segment) {
        return IntStream.range(1, segment.size() - 1)
                .filter(idx -> isPositivePeak(segment.get(idx)))
                .map(segment::get)
                .toArray();
    }

    private boolean isPositivePeak(int index) {
        return isPossibleToDefineThreeValuesSignal(index)
                && isGreenBar(index)
                && isRedBar(index + 1);
    }

    private boolean isRedBar(int index) {
        return !isGreenBar(index);
    }

    private boolean isGreenBar(int index) {
        return indicatorResults[index].getIncreased();
    }

    private Signal[] mergeTwoPeaksSignals(Signal[] buySignals, Signal[] sellSignals) {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new SignalMerger().merge(buySignals[idx], sellSignals[idx]))
                .toArray(Signal[]::new);
    }

    private void buildAOAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, signals.length)
                .mapToObj(idx -> buildAOAnalyzerResult(signals[idx], idx))
                .toArray(AOAnalyzerResult[]::new);
    }

    private AOAnalyzerResult buildAOAnalyzerResult(Signal signal, int currentIndex) {
        return new AOAnalyzerResult(indicatorResults[currentIndex].getTime(), signal);
    }

}
