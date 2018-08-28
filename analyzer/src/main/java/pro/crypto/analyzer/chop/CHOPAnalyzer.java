package pro.crypto.analyzer.chop;

import pro.crypto.indicator.chop.CHOPResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class CHOPAnalyzer implements Analyzer<CHOPAnalyzerResult> {

    private final CHOPResult[] indicatorResults;

    private CHOPAnalyzerResult[] result;

    public CHOPAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (CHOPResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        buildCHOPAnalyzerResult();
    }

    @Override
    public CHOPAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void buildCHOPAnalyzerResult() {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new CHOPAnalyzerResult(indicatorResults[idx].getTime(), defineTrend(idx)))
                .toArray(CHOPAnalyzerResult[]::new);
    }

    private boolean defineTrend(int currentIndex) {
        return isPossibleDefineTrend(currentIndex) && tryDefineTrend(currentIndex);
    }

    private boolean isPossibleDefineTrend(int currentIndex) {
        return nonNull(indicatorResults[currentIndex].getIndicatorValue());
    }

    private boolean tryDefineTrend(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(new BigDecimal(61.8)) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(new BigDecimal(38.2)) > 0;
    }

}
