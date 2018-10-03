package pro.crypto.analyzer.kvo;

import pro.crypto.helper.DynamicLineCrossFinder;
import pro.crypto.indicator.kvo.KVOResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.helper.IndicatorResultExtractor.extractIndicatorValue;
import static pro.crypto.helper.IndicatorResultExtractor.extractSignalLineValues;

public class KVOAnalyzer implements Analyzer<KVOAnalyzerResult> {

    private final KVOResult[] indicatorResults;

    private KVOAnalyzerResult[] result;

    public KVOAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (KVOResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] signals = findSignalLineCrossSignals();
        buildKVOAnalyzerResult(signals);
    }

    @Override
    public KVOAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findSignalLineCrossSignals() {
        return new DynamicLineCrossFinder(extractIndicatorValue(indicatorResults), extractSignalLineValues(indicatorResults)).find();
    }

    private void buildKVOAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new KVOAnalyzerResult(indicatorResults[idx].getTime(), signals[idx]))
                .toArray(KVOAnalyzerResult[]::new);
    }

}
