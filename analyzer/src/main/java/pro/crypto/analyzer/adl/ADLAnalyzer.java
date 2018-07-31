package pro.crypto.analyzer.adl;

import pro.crypto.analyzer.helper.divergence.Divergence;
import pro.crypto.analyzer.helper.divergence.DivergenceRequest;
import pro.crypto.analyzer.helper.divergence.DivergenceResult;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.adl.ADLResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;
import pro.crypto.model.tick.Tick;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.analyzer.helper.divergence.DivergenceClass.A_CLASS;
import static pro.crypto.analyzer.helper.divergence.DivergenceClass.B_CLASS;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.NEUTRAL;
import static pro.crypto.model.Signal.SELL;

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
                if (lastPriceLowerPrevious(divergence.getIndexTo()) && isClassAdmissible(divergence)) {
                    signals[divergence.getIndexTo() + 1] = SELL;
                }
                break;
            case BULLISH:
                if (!lastPriceLowerPrevious(divergence.getIndexTo()) && isClassAdmissible(divergence)) {
                    signals[divergence.getIndexTo() + 1] = BUY;
                }
                break;
            default: /*NOP*/
        }
    }

    private boolean lastPriceLowerPrevious(int indexTo) {
        return priceExist(indexTo + 1) && originalData[indexTo + 1].getClose().compareTo(originalData[indexTo].getClose()) < 0;
    }

    private boolean priceExist(int priceIndex) {
        return priceIndex <= originalData.length;
    }

    private boolean isClassAdmissible(DivergenceResult divergence) {
        return divergence.getDivergenceClass() == A_CLASS || divergence.getDivergenceClass() == B_CLASS;
    }

    private void buildADLAnalyzerResults(Signal[] signals) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> buildADLAnalyzerResult(idx, signals[idx]))
                .toArray(ADLAnalyzerResult[]::new);
    }

    private ADLAnalyzerResult buildADLAnalyzerResult(int currentIndex, Signal signal) {
        return new ADLAnalyzerResult(
                originalData[currentIndex].getTickTime(),
                isNull(signal) ? NEUTRAL : signal,
                indicatorResults[currentIndex].getIndicatorValue(),
                originalData[currentIndex].getClose()
                );
    }

}
