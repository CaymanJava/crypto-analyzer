package pro.crypto.analyzer.cog;

import pro.crypto.helper.DynamicLineCrossFinder;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.cog.COGResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        return new DynamicLineCrossFinder(IndicatorResultExtractor.extract(indicatorResults), extractSignalLineValues()).find();
    }

    private BigDecimal[] extractSignalLineValues() {
        return Stream.of(indicatorResults)
                .map(COGResult::getSignalLineValue)
                .toArray(BigDecimal[]::new);
    }

    private void buildCOGAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new COGAnalyzerResult(indicatorResults[idx].getTime(), signals[idx]))
                .toArray(COGAnalyzerResult[]::new);
    }

}
