package pro.crypto.analyzer.psar;

import pro.crypto.indicator.psar.PSARResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;
import pro.crypto.model.tick.Tick;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.NEUTRAL;
import static pro.crypto.model.Signal.SELL;

public class PSARAnalyzer implements Analyzer<PSARAnalyzerResult> {

    private final Tick[] originalData;
    private final PSARResult[] indicatorResults;

    private PSARAnalyzerResult[] result;

    public PSARAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (PSARResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] signals = findSignals();
        buildPSARAnalyzerResult(signals);
    }

    @Override
    public PSARAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findSignals() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::findSignal)
                .toArray(Signal[]::new);
    }

    private Signal findSignal(int currentIndex) {
        return isPossibleDefineSignal(currentIndex)
                ? defineSignal(currentIndex)
                : NEUTRAL;
    }

    private boolean isPossibleDefineSignal(int currentIndex) {
        return currentIndex > 0
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getIndicatorValue());
    }

    private Signal defineSignal(int currentIndex) {
        if (isBuySignal(currentIndex)) {
            return BUY;
        }

        if (isSellSignal(currentIndex)) {
            return SELL;
        }

        return NEUTRAL;
    }

    private boolean isBuySignal(int currentIndex) {
        return isIndicatorMoreClose(currentIndex - 1) && isIndicatorLessClose(currentIndex);
    }

    private boolean isSellSignal(int currentIndex) {
        return isIndicatorLessClose(currentIndex - 1) && isIndicatorMoreClose(currentIndex);
    }

    private boolean isIndicatorMoreClose(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(originalData[currentIndex].getClose()) > 0;
    }

    private boolean isIndicatorLessClose(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(originalData[currentIndex].getClose()) < 0;
    }

    private void buildPSARAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new PSARAnalyzerResult(indicatorResults[idx].getTime(), signals[idx]))
                .toArray(PSARAnalyzerResult[]::new);
    }

}
