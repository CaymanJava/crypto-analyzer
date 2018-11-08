package pro.crypto.analyzer.vi;

import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.indicator.vi.VIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.SignalStrength;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Strength.STRONG;
import static pro.crypto.model.Strength.WEAK;

public class ViAnalyzer implements Analyzer<VIAnalyzerResult> {

    private final VIResult[] indicatorResults;

    private BigDecimal[] indicatorValues;
    private VIAnalyzerResult[] result;

    public ViAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (VIResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        SignalStrength[] signalLineCrossSignals = findSignalLineCrossSignals();
        SignalStrength[] increaseDecreaseSignals = findIncreaseDecreaseSignals();
        SignalStrength[] mergedSignals = SignalArrayMerger.mergeSignalsStrength(signalLineCrossSignals, increaseDecreaseSignals);
        buildVIAnalyzerResult(mergedSignals);
    }

    @Override
    public VIAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private SignalStrength[] findSignalLineCrossSignals() {
        return Stream.of(new DynamicLineCrossAnalyzer(indicatorValues, IndicatorResultExtractor.extractSignalLineValues(indicatorResults))
                .analyze())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findIncreaseDecreaseSignals() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::findIncreaseDecreaseSignal)
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength findIncreaseDecreaseSignal(int currentIndex) {
        return isPossibleDefineSignal(currentIndex)
                ? defineIncreaseDecreaseSignal(currentIndex)
                : null;
    }

    private boolean isPossibleDefineSignal(int currentIndex) {
        return currentIndex > 0
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getIndicatorValue());
    }

    private SignalStrength defineIncreaseDecreaseSignal(int currentIndex) {
        if (isBuySignal(currentIndex)) {
            return new SignalStrength(BUY, WEAK);
        }

        if (isSellSignal(currentIndex)) {
            return new SignalStrength(SELL, WEAK);
        }

        return null;
    }

    private boolean isBuySignal(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getIndicatorValue()) > 0;
    }

    private boolean isSellSignal(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getIndicatorValue()) < 0;
    }

    private void buildVIAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new VIAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx]))
                .toArray(VIAnalyzerResult[]::new);
    }

}
