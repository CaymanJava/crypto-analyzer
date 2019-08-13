package pro.crypto.analyzer.cog;

import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.cog.COGResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.Signal;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;

public class COGAnalyzer implements Analyzer<COGAnalyzerResult> {

    private final COGResult[] indicatorResults;

    private COGAnalyzerResult[] result;

    public COGAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (COGResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] signals = findSignals();
        buildCOGAnalyzerResult(signals);
    }

    @Override
    public COGAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findSignals() {
        return new DynamicLineCrossAnalyzer(
                IndicatorResultExtractor.extractIndicatorValues(indicatorResults),
                IndicatorResultExtractor.extractSignalLineValues(indicatorResults))
                .analyze();
    }

    private void buildCOGAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new COGAnalyzerResult(indicatorResults[idx].getTime(), signals[idx]))
                .toArray(COGAnalyzerResult[]::new);
    }

}
