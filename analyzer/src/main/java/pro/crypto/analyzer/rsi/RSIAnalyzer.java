package pro.crypto.analyzer.rsi;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.SecurityLevelAnalyzer;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.rsi.RSIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.SecurityLevel;
import pro.crypto.model.analyzer.Signal;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.analyzer.Signal.BUY;
import static pro.crypto.model.analyzer.Signal.SELL;

/**
 * This analyzer is made for RSI and for all indicator, which base on it
 */
public class RSIAnalyzer implements Analyzer<RSIAnalyzerResult> {

    private final RSIResult[] indicatorResults;
    private final BigDecimal oversoldLevel;
    private final BigDecimal overboughtLevel;

    private BigDecimal[] indicatorValues;
    private RSIAnalyzerResult[] result;

    public RSIAnalyzer(AnalyzerRequest analyzerRequest) {
        RSIAnalyzerRequest request = (RSIAnalyzerRequest) analyzerRequest;
        this.indicatorResults = (RSIResult[]) request.getIndicatorResults();
        this.oversoldLevel = extractOversoldLevel(request);
        this.overboughtLevel = extractOverboughtLevel(request);
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

    private BigDecimal extractOversoldLevel(RSIAnalyzerRequest request) {
        return ofNullable(request.getOversoldLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(20));
    }

    private BigDecimal extractOverboughtLevel(RSIAnalyzerRequest request) {
        return ofNullable(request.getOverboughtLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(80));
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private Signal[] findSignals() {
        Signal[] overboughtCrossSignals = findOverboughtCrossSignals();
        Signal[] oversoldCrossSignals = findOversoldCrossSignals();
        return SignalArrayMerger.mergeSignals(overboughtCrossSignals, oversoldCrossSignals);
    }

    private Signal[] findOverboughtCrossSignals() {
        return new StaticLineCrossAnalyzer(indicatorValues, overboughtLevel)
                .withRemovingFalsePositive(BUY)
                .analyze();
    }

    private Signal[] findOversoldCrossSignals() {
        return new StaticLineCrossAnalyzer(indicatorValues, oversoldLevel)
                .withRemovingFalsePositive(SELL)
                .analyze();
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
