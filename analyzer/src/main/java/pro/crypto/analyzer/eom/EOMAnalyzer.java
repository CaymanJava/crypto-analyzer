package pro.crypto.analyzer.eom;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.eom.EOMResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.Signal;

import java.util.stream.IntStream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;

public class EOMAnalyzer implements Analyzer<EOMAnalyzerResult> {

    private final EOMResult[] indicatorResults;

    public EOMAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (EOMResult[]) request.getIndicatorResults();
    }

    private EOMAnalyzerResult[] result;

    @Override
    public void analyze() {
        Signal[] signals = findSignals();
        buildEOMAnalyzerResult(signals);
    }

    @Override
    public EOMAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findSignals() {
        return new StaticLineCrossAnalyzer(IndicatorResultExtractor.extractIndicatorValues(indicatorResults), ZERO).analyze();
    }

    private void buildEOMAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new EOMAnalyzerResult(indicatorResults[idx].getTime(), signals[idx]))
                .toArray(EOMAnalyzerResult[]::new);
    }

}
