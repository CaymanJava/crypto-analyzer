package pro.crypto.analyzer.imi;

import pro.crypto.helper.DefaultDivergenceAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.StaticLineCrossFinder;
import pro.crypto.indicator.imi.IMIResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.SecurityLevel.*;
import static pro.crypto.model.SecurityLevel.UNDEFINED;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Strength.NORMAL;
import static pro.crypto.model.Strength.*;

public class IMIAnalyzer implements Analyzer<IMIAnalyzerResult> {

    private final static BigDecimal OVERBOUGHT_LEVEL = new BigDecimal(70);
    private final static BigDecimal OVERSOLD_LEVEL = new BigDecimal(30);

    private final Tick[] originalData;
    private final IMIResult[] indicatorResults;

    private BigDecimal[] indicatorValues;
    private IMIAnalyzerResult[] result;

    public IMIAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (IMIResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        SignalStrength[] divergenceSignals = findDivergenceSignals();
        SignalStrength[] crossLevelsSignals = findCrossLevelsSignals();
        SignalStrength[] mergedSignals = mergeSignalsStrength(divergenceSignals, crossLevelsSignals);
        SecurityLevel[] securityLevels = findSecurityLevels();
        buildIMIAnalyzerResult(mergedSignals, securityLevels);
    }

    @Override
    public IMIAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValue(indicatorResults);
    }

    private SignalStrength[] findDivergenceSignals() {
        return Stream.of(new DefaultDivergenceAnalyzer().analyze(originalData, indicatorValues))
                .map(signal -> toSignalStrength(signal, WEAK))
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength[] findCrossLevelsSignals() {
        SignalStrength[] overboughtSignals = findOverboughtSignals();
        SignalStrength[] oversoldSignals = findOversoldSignals();
        return mergeSignalsStrength(overboughtSignals, oversoldSignals);
    }

    private SignalStrength[] findOverboughtSignals() {
        return Stream.of(new StaticLineCrossFinder(indicatorValues, OVERBOUGHT_LEVEL).find())
                .map(signal -> toSignalStrength(signal, defineOverboughtStrength(signal)))
                .toArray(SignalStrength[]::new);
    }

    private Strength defineOverboughtStrength(Signal signal) {
        if (isNull(signal)) {
            return null;
        }

        if (signal == SELL) {
            return STRONG;
        }

        if (signal == BUY) {
            return NORMAL;
        }

        return null;
    }

    private SignalStrength[] findOversoldSignals() {
        return Stream.of(new StaticLineCrossFinder(indicatorValues, OVERSOLD_LEVEL).find())
                .map(signal -> toSignalStrength(signal, defineOversoldStrength(signal)))
                .toArray(SignalStrength[]::new);
    }

    private Strength defineOversoldStrength(Signal signal) {
        if (isNull(signal)) {
            return null;
        }

        if (signal == BUY) {
            return STRONG;
        }

        if (signal == SELL) {
            return NORMAL;
        }

        return null;
    }

    private SecurityLevel[] findSecurityLevels() {
        return Stream.of(indicatorValues)
                .map(this::findSecurityLevel)
                .toArray(SecurityLevel[]::new);
    }

    private SecurityLevel findSecurityLevel(BigDecimal indicatorValue) {
        return nonNull(indicatorValue)
                ? defineSecurityLevel(indicatorValue)
                : UNDEFINED;
    }

    private SecurityLevel defineSecurityLevel(BigDecimal indicatorValue) {
        if (indicatorValue.compareTo(OVERBOUGHT_LEVEL) > 0) {
            return OVERBOUGHT;
        }

        if (indicatorValue.compareTo(OVERSOLD_LEVEL) < 0) {
            return OVERSOLD;
        }

        return SecurityLevel.NORMAL;
    }

    private void buildIMIAnalyzerResult(SignalStrength[] mergedSignals, SecurityLevel[] securityLevels) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new IMIAnalyzerResult(indicatorResults[idx].getTime(), mergedSignals[idx], securityLevels[idx]))
                .toArray(IMIAnalyzerResult[]::new);
    }

}
