package pro.crypto.analyzer.ce;

import pro.crypto.analyzer.helper.SignalMerger;
import pro.crypto.indicator.ce.CEResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;

public class CEAnalyzer implements Analyzer<CEAnalyzerResult> {

    private final Tick[] originalData;
    private final CEResult[] indicatorResults;

    private CEAnalyzerResult[] result;

    public CEAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (CEResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] longExits = findLongExitSignals();
        Signal[] shortExits = findShortExitSignals();
        Signal[] mergedSignals = mergeSignals(longExits, shortExits);
        buildCEAnalyzerResult(mergedSignals);
    }

    @Override
    public CEAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findLongExitSignals() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::findLongExitSignal)
                .toArray(Signal[]::new);
    }

    private Signal findLongExitSignal(int currentIndex) {
        return isPossibleDefineSignal(currentIndex, CEResult::getLongChandelierExit)
                && isIntersection(currentIndex, CEResult::getLongChandelierExit)
                ? defineLongExitSignal(currentIndex)
                : null;
    }

    private Signal defineLongExitSignal(int currentIndex) {
        return isUpDownCross(currentIndex)
                ? SELL
                : null;
    }

    private boolean isUpDownCross(int currentIndex) {
        return originalData[currentIndex - 1].getClose()
                .compareTo(indicatorResults[currentIndex - 1].getLongChandelierExit()) > 0
                && originalData[currentIndex].getClose()
                .compareTo(indicatorResults[currentIndex].getLongChandelierExit()) <= 0;
    }

    private Signal[] findShortExitSignals() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::findShortExitSignal)
                .toArray(Signal[]::new);
    }

    private Signal findShortExitSignal(int currentIndex) {
        return isPossibleDefineSignal(currentIndex, CEResult::getShortChandelierExit)
                && isIntersection(currentIndex, CEResult::getShortChandelierExit)
                ? defineShortExitSignal(currentIndex)
                : null;
    }

    private Signal defineShortExitSignal(int currentIndex) {
        return isDownUpCross(currentIndex)
                ? BUY
                : null;
    }

    private boolean isDownUpCross(int currentIndex) {
        return originalData[currentIndex - 1].getClose()
                .compareTo(indicatorResults[currentIndex - 1].getShortChandelierExit()) < 0
                && originalData[currentIndex].getClose()
                .compareTo(indicatorResults[currentIndex].getShortChandelierExit()) >= 0;
    }

    private boolean isIntersection(int currentIndex, Function<CEResult, BigDecimal> getExitFunction) {
        return originalData[currentIndex - 1].getClose()
                .compareTo(getExitFunction.apply(indicatorResults[currentIndex - 1]))
                != originalData[currentIndex].getClose()
                .compareTo(getExitFunction.apply(indicatorResults[currentIndex]));
    }

    private boolean isPossibleDefineSignal(int currentIndex, Function<CEResult, BigDecimal> getExitFunction) {
        return currentIndex > 0
                && nonNull(getExitFunction.apply(indicatorResults[currentIndex - 1]))
                && nonNull(getExitFunction.apply(indicatorResults[currentIndex]));
    }

    private Signal[] mergeSignals(Signal[] longExits, Signal[] shortExits) {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new SignalMerger().merge(longExits[idx], shortExits[idx]))
                .toArray(Signal[]::new);
    }

    private void buildCEAnalyzerResult(Signal[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> buildCEAnalyzerResult(mergedSignals[idx], idx))
                .toArray(CEAnalyzerResult[]::new);
    }

    private CEAnalyzerResult buildCEAnalyzerResult(Signal signal, int currentIndex) {
        return new CEAnalyzerResult(indicatorResults[currentIndex].getTime(), signal);
    }

}
