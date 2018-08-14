package pro.crypto.analyzer.bb;

import pro.crypto.indicator.bb.BBResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.AnalyzerResult;
import pro.crypto.model.tick.Tick;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.analyzer.helper.BandAnalyzer.*;
import static pro.crypto.model.Signal.NEUTRAL;

public class BBAnalyzer implements Analyzer<BBAnalyzerResult> {

    private final Tick[] originalData;
    private final BBResult[] indicatorResults;

    private BBAnalyzerResult[] result;

    public BBAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (BBResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(this::buildBBResult)
                .toArray(BBAnalyzerResult[]::new);
    }

    @Override
    public BBAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private AnalyzerResult buildBBResult(int currentIndex) {
        return new BBAnalyzerResult(
                originalData[currentIndex].getTickTime(), NEUTRAL,
                indicatorResults[currentIndex].getMiddleBand(), originalData[currentIndex].getClose(),
                isUpperBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex]),
                isLowerBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex]),
                isMiddleBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex])
        );
    }

}
