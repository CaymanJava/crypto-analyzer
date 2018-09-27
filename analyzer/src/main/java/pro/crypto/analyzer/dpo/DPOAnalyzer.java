package pro.crypto.analyzer.dpo;

import pro.crypto.helper.StaticLineCrossFinder;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.dpo.DPOResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;

import java.util.stream.IntStream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;

public class DPOAnalyzer implements Analyzer<DPOAnalyzerResult> {

    private final DPOResult[] indicatorResults;

    private DPOAnalyzerResult[] result;

    public DPOAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (DPOResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] signals = findSignals();
        buildCOGAnalyzerResult(signals);
    }

    @Override
    public DPOAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findSignals() {
        return new StaticLineCrossFinder(IndicatorResultExtractor.extract(indicatorResults), ZERO).find();
    }

    private void buildCOGAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new DPOAnalyzerResult(indicatorResults[idx].getTime(), signals[idx]))
                .toArray(DPOAnalyzerResult[]::new);
    }

}
