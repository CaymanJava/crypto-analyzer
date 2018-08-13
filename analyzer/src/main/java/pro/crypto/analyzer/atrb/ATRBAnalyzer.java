package pro.crypto.analyzer.atrb;

import pro.crypto.indicator.atrb.ATRBResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.*;

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

    private ATRBAnalyzerResult buildATRBResult(int currentIndex) {
        return new ATRBAnalyzerResult(
                originalData[currentIndex].getTickTime(), findATRBSignal(currentIndex),
                indicatorResults[currentIndex].getMiddleBand(), originalData[currentIndex].getClose()
        );
    }

    private Signal findATRBSignal(int currentIndex) {
        return isPossibleDefineSignal(currentIndex)
                ? defineATRBSignal(currentIndex)
                : NEUTRAL;
    }

    private boolean isPossibleDefineSignal(int currentIndex) {
        return nonNull(indicatorResults[currentIndex].getMiddleBand())
                && nonNull(indicatorResults[currentIndex].getLowerBand())
                && nonNull(indicatorResults[currentIndex].getUpperBand());
    }

    private Signal defineATRBSignal(int currentIndex) {
        if (isUpperBandCrossPriceRange(currentIndex) && isLowerBandCrossPriceRange(currentIndex)) {
            return NEUTRAL;
        }

        if (isUpperBandCrossPriceRange(currentIndex)) {
            return SELL;
        }

        if (isLowerBandCrossPriceRange(currentIndex)) {
            return BUY;
        }

        return NEUTRAL;
    }

    private boolean isUpperBandCrossPriceRange(int currentIndex) {
        return isBandCrossPriceRange(indicatorResults[currentIndex].getUpperBand(), currentIndex);
    }

    private boolean isLowerBandCrossPriceRange(int currentIndex) {
        return isBandCrossPriceRange(indicatorResults[currentIndex].getLowerBand(), currentIndex);
    }

    private boolean isBandCrossPriceRange(BigDecimal band, int currentIndex) {
        return originalData[currentIndex].getHigh().compareTo(band) >= 0
                && originalData[currentIndex].getLow().compareTo(band) <= 0;
    }

}
