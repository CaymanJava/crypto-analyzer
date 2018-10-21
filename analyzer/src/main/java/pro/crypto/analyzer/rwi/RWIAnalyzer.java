package pro.crypto.analyzer.rwi;

import pro.crypto.indicator.rwi.RWIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;
import pro.crypto.model.Trend;

import java.util.stream.IntStream;

import static java.math.BigDecimal.ONE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Trend.*;

public class RWIAnalyzer implements Analyzer<RWIAnalyzerResult> {

    private final RWIResult[] indicatorResults;

    private Trend[] trends;
    private RWIAnalyzerResult[] result;

    public RWIAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (RWIResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        defineTrends();
        Signal[] signals = findSignals();
        buildRWIAnalyzerResult(signals);
    }

    @Override
    public RWIAnalyzerResult[] getResult() {
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
        return isPossibleDefineTrend(currentIndex)
                ? defineTrend(currentIndex)
                : UNDEFINED;
    }

    private boolean isPossibleDefineTrend(int currentIndex) {
        return nonNull(indicatorResults[currentIndex].getHighValue())
                && nonNull(indicatorResults[currentIndex].getLowValue());
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
        return indicatorResults[currentIndex].getHighValue().compareTo(ONE) > 0
                && indicatorResults[currentIndex].getLowValue().compareTo(ONE) < 0;
    }

    private boolean isDowntrend(int currentIndex) {
        return indicatorResults[currentIndex].getHighValue().compareTo(ONE) < 0
                && indicatorResults[currentIndex].getLowValue().compareTo(ONE) > 0;
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
                && nonNull(trends[currentIndex - 1])
                && nonNull(trends[currentIndex]);
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
        return trends[currentIndex] == UPTREND
                && trends[currentIndex - 1] != UPTREND;
    }

    private boolean isSellSignal(int currentIndex) {
        return trends[currentIndex] == DOWNTREND
                && trends[currentIndex - 1] != DOWNTREND;
    }

    private void buildRWIAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new RWIAnalyzerResult(indicatorResults[idx].getTime(), signals[idx], trends[idx]))
                .toArray(RWIAnalyzerResult[]::new);
    }

}
