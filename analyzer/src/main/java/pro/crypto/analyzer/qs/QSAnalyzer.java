package pro.crypto.analyzer.qs;

import pro.crypto.helper.DefaultDivergenceAnalyzer;
import pro.crypto.helper.IncreaseDecreaseAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.qs.QSResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.analyzer.Strength.NORMAL;
import static pro.crypto.model.analyzer.Strength.STRONG;
import static pro.crypto.model.analyzer.Strength.WEAK;

public class QSAnalyzer implements Analyzer<QSAnalyzerResult> {

    private final Tick[] originalData;
    private final QSResult[] indicatorResults;

    private BigDecimal[] indicatorValues;
    private QSAnalyzerResult[] result;

    public QSAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (QSResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        SignalStrength[] divergenceSignals = findDivergenceSignals();
        SignalStrength[] zeroLineCrossSignals = findZeroLineCrossSignals();
        SignalStrength[] increasedSignals = findIncreasedSignals();
        SignalStrength[] mergedSignals = SignalArrayMerger.mergeSignalsStrength(divergenceSignals, zeroLineCrossSignals, increasedSignals);
        buildQSAnalyzerResult(mergedSignals);
    }

    @Override
    public QSAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private SignalStrength[] findDivergenceSignals() {
        return Stream.of(new DefaultDivergenceAnalyzer(originalData, indicatorValues).analyze())
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findZeroLineCrossSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, BigDecimal.ZERO).analyze())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findIncreasedSignals() {
        return Stream.of(new IncreaseDecreaseAnalyzer(indicatorValues).analyze())
                .map(signal -> toSignalStrength(signal, NORMAL))
                .toArray(SignalStrength[]::new);
    }

    private void buildQSAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new QSAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx]))
                .toArray(QSAnalyzerResult[]::new);
    }

}
