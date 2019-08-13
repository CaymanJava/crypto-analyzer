package pro.crypto.analyzer.cc;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.cc.CCResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.Signal;

import java.util.stream.IntStream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;

public class CCAnalyzer implements Analyzer<CCAnalyzerResult> {

    private final CCResult[] indicatorResults;

    private CCAnalyzerResult[] result;

    public CCAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (CCResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] crossSignals = findCrossSignals();
        buildCCAnalyzerResult(crossSignals);
    }

    @Override
    public CCAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findCrossSignals() {
        return new StaticLineCrossAnalyzer(IndicatorResultExtractor.extractIndicatorValues(indicatorResults), ZERO).analyze();
    }

    private void buildCCAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new CCAnalyzerResult(indicatorResults[idx].getTime(), signals[idx]))
                .toArray(CCAnalyzerResult[]::new);
    }

}
