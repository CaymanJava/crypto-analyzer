package pro.crypto.analyzer.dc;

import pro.crypto.indicator.dc.DCResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.tick.Tick;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.helper.BandAnalyzer.*;

public class DCAnalyzer implements Analyzer<DCAnalyzerResult> {

    private final Tick[] originalData;
    private final DCResult[] indicatorResults;

    private DCAnalyzerResult[] result;

    public DCAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (DCResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(this::buildDCResult)
                .toArray(DCAnalyzerResult[]::new);
    }

    @Override
    public DCAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private DCAnalyzerResult buildDCResult(int currentIndex) {
        return new DCAnalyzerResult(
                indicatorResults[currentIndex].getTime(),
                isUpperBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex]),
                isLowerBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex]),
                isMiddleBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex])
        );
    }

}
