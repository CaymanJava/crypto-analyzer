package pro.crypto.analyzer.rsi;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.SecurityLevelAnalyzer;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.rsi.RSIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.SecurityLevel;
import pro.crypto.model.Signal;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;

/**
 * This analyzer is made for RSI and for all indicator, which base on it
 */
public class RSIAnalyzer implements Analyzer<RSIAnalyzerResult>{

    private final RSIResult[] indicatorResults;
    private final BigDecimal overboughtLevel;
    private final BigDecimal oversoldLevel;

    private BigDecimal[] indicatorValues;
    private RSIAnalyzerResult[] result;

    public RSIAnalyzer(AnalyzerRequest analyzerRequest) {
        RSIAnalyzerRequest request = (RSIAnalyzerRequest) analyzerRequest;
        this.indicatorResults = (RSIResult[]) request.getIndicatorResults();
        this.overboughtLevel = isNull(request.getOverboughtLevel()) ? new BigDecimal(80) : new BigDecimal(request.getOverboughtLevel());
        this.oversoldLevel = isNull(request.getOversoldLevel()) ? new BigDecimal(20) : new BigDecimal(request.getOversoldLevel());
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        Signal[] signals = findSignals();
        SecurityLevel[] securityLevels = defineSecurityLevels();
        buildRSIAnalyzerRequest(signals, securityLevels);
    }

    @Override
    public RSIAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private Signal[] findSignals() {
        Signal[] overboughtCrossSignals = findOverboughtCrossSignals();
        Signal[] oversoldCrossSignals = findOversoldCrossSignals();
        return mergeSignals(overboughtCrossSignals, oversoldCrossSignals);
    }

    private Signal[] findOverboughtCrossSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, overboughtLevel).analyze())
                .map(signal -> removeFalsePositiveSignal(signal, BUY))
                .toArray(Signal[]::new);
    }

    private Signal[] findOversoldCrossSignals() {
        return Stream.of(new StaticLineCrossAnalyzer(indicatorValues, oversoldLevel).analyze())
                .map(signal -> removeFalsePositiveSignal(signal, SELL))
                .toArray(Signal[]::new);
    }

    private SecurityLevel[] defineSecurityLevels() {
        return new SecurityLevelAnalyzer(indicatorValues, overboughtLevel, oversoldLevel).analyze();
    }

    private void buildRSIAnalyzerRequest(Signal[] signals, SecurityLevel[] securityLevels) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new RSIAnalyzerResult(indicatorResults[idx].getTime(), signals[idx], securityLevels[idx]))
                .toArray(RSIAnalyzerResult[]::new);
    }

}
