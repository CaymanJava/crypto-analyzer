package pro.crypto.analyzer.cog;

import pro.crypto.indicator.cog.COGResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.NEUTRAL;
import static pro.crypto.model.Signal.SELL;

public class COGAnalyzer implements Analyzer<COGAnalyzerResult> {

    private final COGResult[] indicatorResults;

    private COGAnalyzerResult[] result;

    public COGAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (COGResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        buildCOGAnalyzerResult();
    }

    @Override
    public COGAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void buildCOGAnalyzerResult() {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new COGAnalyzerResult(indicatorResults[idx].getTime(), findSignal(idx)))
                .toArray(COGAnalyzerResult[]::new);
    }

    private Signal findSignal(int currentIndex) {
        return isPossibleDefineSignal(currentIndex)
                ? tryDefineSignal(currentIndex)
                : NEUTRAL;
    }

    private boolean isPossibleDefineSignal(int currentIndex) {
        return currentIndex > 0
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex - 1].getSignalLineValue())
                && nonNull(indicatorResults[currentIndex].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getSignalLineValue());
    }

    private Signal tryDefineSignal(int currentIndex) {
        if (isSignalLineIntersection(currentIndex)) {
            return defineSignal(currentIndex);
        }
        return NEUTRAL;
    }

    private boolean isSignalLineIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getSignalLineValue())
                != indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex].getSignalLineValue());
    }

    private Signal defineSignal(int currentIndex) {
        if (isBuyIntersection(currentIndex)) {
            return BUY;
        }

        if (isSellIntersection(currentIndex)) {
            return SELL;
        }

        return NEUTRAL;
    }

    private boolean isBuyIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getSignalLineValue()) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex].getSignalLineValue()) > 0;
    }

    private boolean isSellIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getSignalLineValue()) > 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex].getSignalLineValue()) < 0;
    }

}
