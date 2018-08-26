package pro.crypto.analyzer.atr;

import pro.crypto.indicator.atr.ATRResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ATRAnalyzer implements Analyzer<ATRAnalyzerResult> {

    private final ATRResult[] indicatorResults;

    private ATRAnalyzerResult[] result;

    public ATRAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (ATRResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(this::buildATRAnalyzerResult)
                .toArray(ATRAnalyzerResult[]::new);
    }

    @Override
    public ATRAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private ATRAnalyzerResult buildATRAnalyzerResult(int currentIndex) {
        return new ATRAnalyzerResult(indicatorResults[currentIndex].getTime(), defineStartTrend(currentIndex), isTrendExist(currentIndex));
    }

    private Boolean defineStartTrend(int currentIndex) {
        return isPossibleToDefineStartTrend(currentIndex) && isStartTrend(currentIndex);
    }

    private boolean isPossibleToDefineStartTrend(int currentIndex) {
        return currentIndex > 0
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex - 1].getSignalLineValue())
                && nonNull(indicatorResults[currentIndex].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getSignalLineValue());
    }

    private boolean isStartTrend(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getSignalLineValue()) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex].getSignalLineValue()) >= 0;
    }

    private boolean isTrendExist(int currentIndex) {
        return nonNull(indicatorResults[currentIndex].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getSignalLineValue())
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex].getSignalLineValue()) >= 0;
    }

}
