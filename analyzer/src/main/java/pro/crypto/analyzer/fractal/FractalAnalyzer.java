package pro.crypto.analyzer.fractal;

import pro.crypto.helper.SignalMerger;
import pro.crypto.indicator.fractal.FractalResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.Signal;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.analyzer.Signal.BUY;
import static pro.crypto.model.analyzer.Signal.SELL;

public class FractalAnalyzer implements Analyzer<FractalAnalyzerResult> {

    private final Tick[] originalData;
    private final FractalResult[] indicatorResults;
    private final SignalMerger signalMerger = new SignalMerger();

    private BigDecimal highPriceLastUpFractal;
    private BigDecimal lowPriceLastDownFractal;

    private FractalAnalyzerResult[] result;

    public FractalAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (FractalResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] signals = findSignals();
        buildFractalAnalyzerResult(signals);
    }

    @Override
    public FractalAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findSignals() {
        return IntStream.range(0, originalData.length)
                .mapToObj(this::findSignal)
                .toArray(Signal[]::new);
    }

    private Signal findSignal(int currentIndex) {
        refreshLastFractals(currentIndex);
        Signal buySignal = findBuySignal(currentIndex);
        Signal sellSignal = findSellSignal(currentIndex);
        return signalMerger.merge(buySignal, sellSignal);
    }

    private void refreshLastFractals(int currentIndex) {
        if (indicatorResults[currentIndex].isUpFractal()) {
            highPriceLastUpFractal = originalData[currentIndex].getHigh();
        }

        if (indicatorResults[currentIndex].isDownFractal()) {
            lowPriceLastDownFractal = originalData[currentIndex].getLow();
        }
    }

    private Signal findBuySignal(int currentIndex) {
        return nonNull(highPriceLastUpFractal)
                ? defineBuySignal(currentIndex)
                : null;
    }

    private Signal defineBuySignal(int currentIndex) {
        if (originalData[currentIndex].getClose().compareTo(highPriceLastUpFractal) > 0) {
            highPriceLastUpFractal = null;
            return BUY;
        }
        return null;
    }

    private Signal findSellSignal(int currentIndex) {
        return nonNull(lowPriceLastDownFractal)
                ? defineSellSignal(currentIndex)
                : null;
    }

    private Signal defineSellSignal(int currentIndex) {
        if (originalData[currentIndex].getClose().compareTo(lowPriceLastDownFractal) < 0) {
            lowPriceLastDownFractal = null;
            return SELL;
        }
        return null;
    }

    private void buildFractalAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new FractalAnalyzerResult(originalData[idx].getTickTime(), signals[idx]))
                .toArray(FractalAnalyzerResult[]::new);
    }

}
