package pro.crypto.analyzer.si;

import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.si.SIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.Signal;

import java.util.stream.IntStream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static pro.crypto.helper.IndicatorResultExtractor.extractIndicatorValues;

public class SIAnalyzer implements Analyzer<SIAnalyzerResult> {

    private final SIResult[] indicatorResults;

    private SIAnalyzerResult[] result;

    public SIAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (SIResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] signals = findSignals();
        buildSIAnalyzerResult(signals);
    }

    @Override
    public SIAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findSignals() {
        return new StaticLineCrossAnalyzer(extractIndicatorValues(indicatorResults), ZERO).analyze();
    }

    private void buildSIAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new SIAnalyzerResult(indicatorResults[idx].getTime(), signals[idx]))
                .toArray(SIAnalyzerResult[]::new);
    }

}
