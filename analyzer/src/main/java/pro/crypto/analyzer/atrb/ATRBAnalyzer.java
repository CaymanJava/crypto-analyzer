package pro.crypto.analyzer.atrb;

import pro.crypto.indicator.atrb.ATRBResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;
import pro.crypto.model.tick.Tick;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.helper.BandAnalyzer.*;

public class ATRBAnalyzer implements Analyzer<ATRBAnalyzerResult> {

    private final Tick[] originalData;
    private final ATRBResult[] indicatorResults;

    private ATRBAnalyzerResult[] result;

    public ATRBAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (ATRBResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(this::buildATRBResult)
                .toArray(ATRBAnalyzerResult[]::new);
    }

    @Override
    public ATRBAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private AnalyzerResult buildATRBResult(int currentIndex) {
        return new ATRBAnalyzerResult(
                indicatorResults[currentIndex].getTime(),
                isUpperBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex]),
                isLowerBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex]),
                isMiddleBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex])
        );
    }

}
