package pro.crypto.analyzer.ma;

import pro.crypto.helper.DynamicLineCrossFinder;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;
import pro.crypto.model.tick.Tick;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class MAAnalyzer implements Analyzer<MAAnalyzerResult> {

    private final Tick[] originalData;
    private final MAResult[] indicatorResults;

    private MAAnalyzerResult[] result;

    public MAAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (MAResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] signals = findCrossPriceSignals();
        buildMAAnalyzerResult(signals);
    }

    @Override
    public MAAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findCrossPriceSignals() {
        return new DynamicLineCrossFinder(
                PriceExtractor.extract(originalData, CLOSE),
                IndicatorResultExtractor.extractIndicatorValue(indicatorResults))
                .find();
    }

    private void buildMAAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, signals.length)
                .mapToObj(idx -> new MAAnalyzerResult(indicatorResults[idx].getTime(), signals[idx]))
                .toArray(MAAnalyzerResult[]::new);
    }

}
