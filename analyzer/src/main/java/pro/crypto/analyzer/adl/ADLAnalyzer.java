package pro.crypto.analyzer.adl;

import pro.crypto.analyzer.helper.divergence.Divergence;
import pro.crypto.analyzer.helper.divergence.DivergenceRequest;
import pro.crypto.analyzer.helper.divergence.DivergenceResult;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.adl.ADLResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.AnalyzerResult;
import pro.crypto.model.Signal;
import pro.crypto.model.tick.Tick;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.Signal.*;

public class ADLAnalyzer implements Analyzer<ADLAnalyzerResult> {

    private final Tick[] originalData;
    private final ADLResult[] indicatorResults;

    private ADLAnalyzerResult[] result;

    public ADLAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (ADLResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        DivergenceResult[] divergences = new Divergence(buildDivergenceRequest()).find();
        recognizeSignals(divergences);
    }

    @Override
    public ADLAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private DivergenceRequest buildDivergenceRequest() {
        return DivergenceRequest.builder()
                .originalData(originalData)
                .indicatorValues(IndicatorResultExtractor.extract(indicatorResults))
                .build();
    }

    private void recognizeSignals(DivergenceResult[] divergences) {
        Signal[] signals = new Signal[originalData.length];
        Stream.of(divergences)
                .forEach(divergence -> recognizeSignal(divergence, signals));
        buildADLAnalyzerResults(signals);
    }

    private void recognizeSignal(DivergenceResult divergence, Signal[] signals) {
        switch (divergence.getDivergenceType()) {
            case BEARER:
                recognizeBearerSignal(divergence, signals);
                break;
            case BULLISH:
                recognizeBullishSignal(divergence, signals);
                break;
            default: /*NOP*/
        }
    }

    private void recognizeBearerSignal(DivergenceResult divergence, Signal[] signals) {
        if (isLastPriceLowerPrevious(divergence.getIndexTo()) /*&& isClassicOrExtended(divergence.getDivergenceClass())*/) {
            signals[divergence.getIndexTo() + 1] = SELL;
        }
    }

    private void recognizeBullishSignal(DivergenceResult divergence, Signal[] signals) {
        if (!isLastPriceLowerPrevious(divergence.getIndexTo())/* && isClassicOrExtended(divergence.getDivergenceClass())*/) {
            signals[divergence.getIndexTo() + 1] = BUY;
        }
    }

    private boolean isLastPriceLowerPrevious(int indexTo) {
        return isPriceExist(indexTo + 1) && originalData[indexTo + 1].getClose().compareTo(originalData[indexTo].getClose()) < 0;
    }

    private boolean isPriceExist(int priceIndex) {
        return priceIndex <= originalData.length;
    }

    private void buildADLAnalyzerResults(Signal[] signals) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> buildADLAnalyzerResult(idx, signals[idx]))
                .toArray(ADLAnalyzerResult[]::new);
    }

    private AnalyzerResult buildADLAnalyzerResult(int currentIndex, Signal signal) {
        return new ADLAnalyzerResult(
                originalData[currentIndex].getTickTime(),
                isNull(signal) ? NEUTRAL : signal,
                indicatorResults[currentIndex].getIndicatorValue(),
                originalData[currentIndex].getClose()
        );
    }

}
