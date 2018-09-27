package pro.crypto.analyzer.hlb;

import pro.crypto.indicator.hlb.HLBResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;
import pro.crypto.model.tick.Tick;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.helper.BandAnalyzer.isLowerBandCrossPriceRange;
import static pro.crypto.helper.BandAnalyzer.isMiddleBandCrossPriceRange;
import static pro.crypto.helper.BandAnalyzer.isUpperBandCrossPriceRange;

public class HLBAnalyzer implements Analyzer<HLBAnalyzerResult> {

    private final Tick[] originalData;
    private final HLBResult[] indicatorResults;

    private HLBAnalyzerResult[] result;

    public HLBAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (HLBResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(this::buildHLBResult)
                .toArray(HLBAnalyzerResult[]::new);
    }

    @Override
    public HLBAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private AnalyzerResult buildHLBResult(int currentIndex) {
        return new HLBAnalyzerResult(
                indicatorResults[currentIndex].getTime(),
                isUpperBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex]),
                isLowerBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex]),
                isMiddleBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex])
        );
    }

}
