package pro.crypto.analyzer.efi;

import pro.crypto.analyzer.helper.StaticLineCrossFinder;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.efi.EFIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;

import java.util.stream.IntStream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;

public class EFIAnalyzer implements Analyzer<EFIAnalyzerResult> {

    private final EFIResult[] indicatorResults;

    private EFIAnalyzerResult[] result;

    public EFIAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (EFIResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {

        Signal[] signals = findSignals();
        buildEFIAnalyzerResult(signals);
    }

    @Override
    public EFIAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findSignals() {
        return new StaticLineCrossFinder(IndicatorResultExtractor.extract(indicatorResults), ZERO).find();
    }

    private void buildEFIAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new EFIAnalyzerResult(indicatorResults[idx].getTime(), signals[idx]))
                .toArray(EFIAnalyzerResult[]::new);
    }

}
