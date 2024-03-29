package pro.crypto.analyzer.env;

import pro.crypto.helper.BandAnalyzer;
import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.helper.PriceVolumeExtractor;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.indicator.env.ENVResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.analyzer.Signal.BUY;
import static pro.crypto.model.analyzer.Signal.SELL;
import static pro.crypto.model.analyzer.Strength.NORMAL;
import static pro.crypto.model.analyzer.Strength.WEAK;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ENVAnalyzer implements Analyzer<ENVAnalyzerResult> {

    private final Tick[] originalData;
    private final ENVResult[] indicatorResults;

    private ENVAnalyzerResult[] result;

    public ENVAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (ENVResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        SignalStrength[] signals = findSignals();
        buildENVAnalyzerResult(signals);
    }

    @Override
    public ENVAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private SignalStrength[] findSignals() {
        BigDecimal[] closePrices = PriceVolumeExtractor.extractPrices(originalData, CLOSE);
        SignalStrength[] upperBandSignals = findUpperBandSignals(closePrices);
        SignalStrength[] lowerBandSignals = findLowerBandSignals(closePrices);
        SignalStrength[] middleBandSignals = findMiddleBandSignals(closePrices);
        return SignalArrayMerger.mergeSignalsStrength(upperBandSignals, lowerBandSignals, middleBandSignals);
    }

    private SignalStrength[] findUpperBandSignals(BigDecimal[] closePrices) {
        return Stream.of(
                new DynamicLineCrossAnalyzer(closePrices, extractBandValues(ENVResult::getUpperBand))
                        .withRemovingFalsePositive(BUY)
                        .analyze())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findLowerBandSignals(BigDecimal[] closePrices) {
        return Stream.of(
                new DynamicLineCrossAnalyzer(closePrices, extractBandValues(ENVResult::getLowerBand))
                        .withRemovingFalsePositive(SELL)
                        .analyze())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findMiddleBandSignals(BigDecimal[] closePrices) {
        return Stream.of(new DynamicLineCrossAnalyzer(closePrices, extractBandValues(ENVResult::getMiddleBand)).analyze())
                .map(signal -> toSignalStrength(signal, NORMAL))
                .toArray(SignalStrength[]::new);
    }

    private BigDecimal[] extractBandValues(Function<ENVResult, BigDecimal> extractFunction) {
        return Stream.of(indicatorResults)
                .map(extractFunction)
                .toArray(BigDecimal[]::new);
    }

    private void buildENVAnalyzerResult(SignalStrength[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> buildENVResult(signals[idx], idx))
                .toArray(ENVAnalyzerResult[]::new);
    }

    private ENVAnalyzerResult buildENVResult(SignalStrength signalStrength, int currentIndex) {
        return new ENVAnalyzerResult(indicatorResults[currentIndex].getTime(), signalStrength,
                BandAnalyzer.isUpperBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex]),
                BandAnalyzer.isLowerBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex]),
                BandAnalyzer.isMiddleBandCrossPriceRange(originalData[currentIndex], indicatorResults[currentIndex])
        );
    }

}
