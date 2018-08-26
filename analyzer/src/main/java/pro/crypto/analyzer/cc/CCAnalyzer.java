package pro.crypto.analyzer.cc;

import pro.crypto.indicator.cc.CCResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.*;

public class CCAnalyzer implements Analyzer<CCAnalyzerResult> {

    private final CCResult[] indicatorResults;

    private CCAnalyzerResult[] result;

    public CCAnalyzer(AnalyzerRequest request) {
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
        return new CCAnalyzerResult(indicatorResults[currentIndex].getTime(), findSignal(currentIndex));
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
