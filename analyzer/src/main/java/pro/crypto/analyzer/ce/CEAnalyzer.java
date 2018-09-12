package pro.crypto.analyzer.ce;

import pro.crypto.analyzer.helper.DynamicLineCrossFinder;
import pro.crypto.analyzer.helper.SignalMerger;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.indicator.ce.CEResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.tick.PriceType.CLOSE;

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
        BigDecimal[] closePrices = PriceExtractor.extractValuesByType(originalData, CLOSE);
        Signal[] longExits = findLongExitSignals(closePrices);
        Signal[] shortExits = findShortExitSignals(closePrices);
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

    private Signal[] findLongExitSignals(BigDecimal[] closePrices) {
        return Stream.of(new DynamicLineCrossFinder(closePrices, extractExits(CEResult::getLongChandelierExit)).find())
                .map(signal -> removeFalsePositiveSignal(signal, BUY))
                .toArray(Signal[]::new);
    }

    private Signal[] findShortExitSignals(BigDecimal[] closePrices) {
        return Stream.of(new DynamicLineCrossFinder(closePrices, extractExits(CEResult::getShortChandelierExit)).find())
                .map(signal -> removeFalsePositiveSignal(signal, SELL))
                .toArray(Signal[]::new);
    }

    private Signal removeFalsePositiveSignal(Signal signal, Signal falsePositive) {
        return signal != falsePositive ? signal : null;
    }

    private BigDecimal[] extractExits(Function<CEResult, BigDecimal> extractExitFunction) {
        return Stream.of(indicatorResults)
                .map(extractExitFunction)
                .toArray(BigDecimal[]::new);
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
