package pro.crypto.analyzer.cfo;

import pro.crypto.indicator.cfo.CFOResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.NEUTRAL;
import static pro.crypto.model.Signal.SELL;

public class CFOAnalyzer implements Analyzer<CFOAnalyzerResult> {

    private final static BigDecimal OVERBOUGHT_LEVEL = new BigDecimal(0.5);
    private final static BigDecimal OVERSOLD_LEVEL = new BigDecimal(-0.5);

    private final Tick[] originalData;
    private final CFOResult[] indicatorResults;

    private CFOAnalyzerResult[] result;

    public CFOAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (CFOResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] signals = findSignals();
        buildCFOAnalyzerResult(signals);
    }

    @Override
    public CFOAnalyzerResult[] getResult() {
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
        return nonNull(indicatorResults[currentIndex].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getSignalLineValue());
    }

    private Signal defineSignal(int currentIndex) {
        if (isIndicatorAboveSignalLine(currentIndex) && !isIndicatorInOversoldLevel(currentIndex)) {
            return BUY;
        }

        if (isIndicatorUnderSignalLine(currentIndex) && !isIndicatorInOverboughtLevel(currentIndex)) {
            return SELL;
        }

        return NEUTRAL;
    }

    private boolean isIndicatorAboveSignalLine(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue()
                .compareTo(indicatorResults[currentIndex].getSignalLineValue()) >= 0;
    }

    private boolean isIndicatorInOversoldLevel(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue()
                .compareTo(OVERSOLD_LEVEL) <= 0;
    }

    private boolean isIndicatorUnderSignalLine(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue()
                .compareTo(indicatorResults[currentIndex].getSignalLineValue()) <= 0;
    }

    private boolean isIndicatorInOverboughtLevel(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue()
                .compareTo(OVERBOUGHT_LEVEL) >= 0;
    }

    private void buildCFOAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new CFOAnalyzerResult(originalData[idx].getTickTime(), signals[idx]))
                .toArray(CFOAnalyzerResult[]::new);
    }

}