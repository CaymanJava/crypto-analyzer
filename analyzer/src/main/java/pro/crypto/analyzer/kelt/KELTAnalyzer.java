package pro.crypto.analyzer.kelt;

import pro.crypto.helper.DynamicLineCrossFinder;
import pro.crypto.helper.PriceVolumeExtractor;
import pro.crypto.indicator.kelt.KELTResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.result.AnalyzerResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.helper.BandAnalyzer.*;
import static pro.crypto.model.Strength.STRONG;
import static pro.crypto.model.Strength.WEAK;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class KELTAnalyzer implements Analyzer<KELTAnalyzerResult> {

    private final Tick[] originalData;
    private final KELTResult[] indicatorResults;

    private BigDecimal[] closePrices;
    private KELTAnalyzerResult[] result;

    public KELTAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (KELTResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        extractClosePrices();
        SignalStrength[] upperBandCrossSignals = findUpperBandCrossSignals();
        SignalStrength[] middleBandCrossSignals = findMiddleBandCrossSignals();
        SignalStrength[] lowerBandCrossSignals = findLowerBandCrossSignals();
        SignalStrength[] mergedSignals = mergeSignalsStrength(upperBandCrossSignals, middleBandCrossSignals, lowerBandCrossSignals);
        buildKELTAnalyzerResult(mergedSignals);
    }

    @Override
    public KELTAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void extractClosePrices() {
        closePrices = PriceVolumeExtractor.extract(originalData, CLOSE);
    }

    private SignalStrength[] findUpperBandCrossSignals() {
        return Stream.of(new DynamicLineCrossFinder(closePrices, extractBand(KELTResult::getUpperBand)).find())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findMiddleBandCrossSignals() {
        return Stream.of(new DynamicLineCrossFinder(closePrices, extractBand(KELTResult::getMiddleBand)).find())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);

    }

    private SignalStrength[] findLowerBandCrossSignals() {
        return Stream.of(new DynamicLineCrossFinder(closePrices, extractBand(KELTResult::getLowerBand)).find())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private BigDecimal[] extractBand(Function<KELTResult, BigDecimal> extractBandFunction) {
        return Stream.of(indicatorResults)
                .map(extractBandFunction)
                .toArray(BigDecimal[]::new);
    }

    private void buildKELTAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> buildKELTResult(mergedSignals[idx], idx))
                .toArray(KELTAnalyzerResult[]::new);
    }

    private AnalyzerResult buildKELTResult(SignalStrength signal, int currentIndex) {
        return new KELTAnalyzerResult(
                indicatorResults[currentIndex].getTime(), signal,
                isUpperBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex]),
                isLowerBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex]),
                isMiddleBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex])
        );
    }

}
