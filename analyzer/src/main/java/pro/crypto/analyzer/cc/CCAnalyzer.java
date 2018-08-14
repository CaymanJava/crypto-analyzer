package pro.crypto.analyzer.cc;

import pro.crypto.indicator.cc.CCResult;
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

public class CCAnalyzer implements Analyzer<CCAnalyzerResult> {

    private final Tick[] originalData;
    private final CCResult[] indicatorResults;

    private CCAnalyzerResult[] result;

    public CCAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (CCResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(this::buildCCAnalyzerResult)
                .toArray(CCAnalyzerResult[]::new);
    }

    @Override
    public CCAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private CCAnalyzerResult buildCCAnalyzerResult(int currentIndex) {
        return new CCAnalyzerResult(
                originalData[currentIndex].getTickTime(), findSignal(currentIndex),
                indicatorResults[currentIndex].getIndicatorValue(), originalData[currentIndex].getClose()
        );
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
        if (isPositiveIntersection(currentIndex)) {
            return BUY;
        }

        if (isNegativeIntersection(currentIndex)) {
            return SELL;
        }

        return NEUTRAL;
    }

    private boolean isPositiveIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(BigDecimal.ZERO) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isNegativeIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(BigDecimal.ZERO) > 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(BigDecimal.ZERO) <= 0;
    }

}
