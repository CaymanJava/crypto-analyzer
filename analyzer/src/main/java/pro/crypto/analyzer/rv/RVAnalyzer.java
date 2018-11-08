package pro.crypto.analyzer.rv;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.rv.RVResult;
import pro.crypto.model.*;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.NEUTRAL;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Strength.NORMAL;
import static pro.crypto.model.Strength.STRONG;
import static pro.crypto.model.Strength.UNDEFINED;

public class RVAnalyzer implements Analyzer<RVAnalyzerResult> {

    private final RVResult[] indicatorResults;
    private final BigDecimal bullishSignalLine;
    private final BigDecimal bearerSignalLine;

    private BigDecimal[] indicatorValues;
    private RVAnalyzerResult[] result;

    public RVAnalyzer(AnalyzerRequest analyzerRequest) {
        RVAnalyzerRequest request = (RVAnalyzerRequest) analyzerRequest;
        this.indicatorResults = (RVResult[]) request.getIndicatorResults();
        this.bullishSignalLine = extractBullishSignalLine(request);
        this.bearerSignalLine = extractBearerSignalLine(request);
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        SignalStrength[] signals = findSignals();
        buildRVAnalyzerResult(signals);
    }

    @Override
    public RVAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractBullishSignalLine(RVAnalyzerRequest request) {
        return ofNullable(request.getBullishSignalLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(50));
    }

    private BigDecimal extractBearerSignalLine(RVAnalyzerRequest request) {
        return ofNullable(request.getBearerSignalLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(50));
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private SignalStrength[] findSignals() {
        return signalLinesTheSame()
                ? findSingleSignalLineSignals()
                : findDoubleSignalLinesSignals();
    }

    private boolean signalLinesTheSame() {
        return bullishSignalLine.compareTo(bearerSignalLine) == 0;
    }

    private SignalStrength[] findSingleSignalLineSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, bullishSignalLine).analyze())
                .map(signal -> toSignalStrength(signal, STRONG))
                .map(this::replaceNullValues)
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength replaceNullValues(SignalStrength signal) {
        return isNull(signal)
                ? new SignalStrength(NEUTRAL, UNDEFINED)
                : signal;
    }

    private SignalStrength[] findDoubleSignalLinesSignals() {
        SignalStrength[] bullishSignals = findBullishLineCrossSignals();
        SignalStrength[] bearerSignals = findBearerLineCrossSignals();
        return SignalArrayMerger.mergeSignalsStrength(bullishSignals, bearerSignals);
    }

    private SignalStrength[] findBullishLineCrossSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, bullishSignalLine).analyze())
                .map(signal -> toSignalStrength(signal, defineBullishLineStrength(signal)))
                .toArray(SignalStrength[]::new);
    }

    private Strength defineBullishLineStrength(Signal signal) {
        if (signal == BUY) {
            return STRONG;
        }

        if (signal == SELL) {
            return NORMAL;
        }

        return null;
    }

    private SignalStrength[] findBearerLineCrossSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, bearerSignalLine).analyze())
                .map(signal -> toSignalStrength(signal, defineBearerLineStrength(signal)))
                .toArray(SignalStrength[]::new);
    }

    private Strength defineBearerLineStrength(Signal signal) {
        if (signal == SELL) {
            return STRONG;
        }

        if (signal == BUY) {
            return NORMAL;
        }

        return null;
    }

    private void buildRVAnalyzerResult(SignalStrength[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new RVAnalyzerResult(indicatorResults[idx].getTime(), signals[idx]))
                .toArray(RVAnalyzerResult[]::new);
    }

}
