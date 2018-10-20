package pro.crypto.analyzer.pgo;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.pgo.PGOResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Strength.STRONG;
import static pro.crypto.model.Strength.WEAK;

public class PGOAnalyzer implements Analyzer<PGOAnalyzerResult> {

    private final static BigDecimal OVERBOUGHT_LEVEL = new BigDecimal(3);
    private final static BigDecimal OVERSOLD_LEVEL = new BigDecimal(-3);

    private final Tick[] originalData;
    private final PGOResult[] indicatorResults;

    private BigDecimal[] indicatorValues;
    private PGOAnalyzerResult[] result;

    public PGOAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (PGOResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        SignalStrength[] overboughtLevelSignals = findOverboughtLevelSignals();
        SignalStrength[] oversoldLeverSignals = findOversoldLevelSignals();
        SignalStrength[] zeroLineCrossSignals = findZeroLineCrossSignals();
        SignalStrength[] mergedSignals = mergeSignalsStrength(overboughtLevelSignals, oversoldLeverSignals, zeroLineCrossSignals);
        buildPGOAnalyzerResult(mergedSignals);
    }

    @Override
    public PGOAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private SignalStrength[] findOverboughtLevelSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, OVERBOUGHT_LEVEL).analyze())
                .map(signal -> toSignalStrength(signal, defineOverboughtStrength(signal)))
                .toArray(SignalStrength[]::new);
    }

    private Strength defineOverboughtStrength(Signal signal) {
        if (signal == BUY) {
            return STRONG;
        }

        if (signal == SELL) {
            return WEAK;
        }

        return null;
    }

    private SignalStrength[] findOversoldLevelSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, OVERSOLD_LEVEL).analyze())
                .map(signal -> toSignalStrength(signal, defineOversoldStrength(signal)))
                .toArray(SignalStrength[]::new);
    }

    private Strength defineOversoldStrength(Signal signal) {
        if (signal == SELL) {
            return STRONG;
        }

        if (signal == BUY) {
            return WEAK;
        }

        return null;
    }

    private SignalStrength[] findZeroLineCrossSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, BigDecimal.ZERO).analyze())
                .map(signal -> toSignalStrength(signal, STRONG))
                .toArray(SignalStrength[]::new);
    }

    private void buildPGOAnalyzerResult(SignalStrength[] mergedSignals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new PGOAnalyzerResult(originalData[idx].getTickTime(), mergedSignals[idx]))
                .toArray(PGOAnalyzerResult[]::new);
    }

}
