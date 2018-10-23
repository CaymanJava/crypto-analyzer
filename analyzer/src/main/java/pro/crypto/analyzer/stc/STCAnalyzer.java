package pro.crypto.analyzer.stc;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.stc.STCResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;

public class STCAnalyzer implements Analyzer<STCAnalyzerResult> {

    private final STCResult[] indicatorResults;
    private final BigDecimal oversoldLevel;
    private final BigDecimal overboughtLevel;

    private BigDecimal[] indicatorValues;
    private STCAnalyzerResult[] result;

    public STCAnalyzer(AnalyzerRequest analyzerRequest) {
        STCAnalyzerRequest request = (STCAnalyzerRequest) analyzerRequest;
        this.indicatorResults = (STCResult[]) request.getIndicatorResults();
        this.oversoldLevel = extractOversoldLevel(request);
        this.overboughtLevel = extractOverboughtLevel(request);
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        Signal[] signals = findSignals();
        buildSTCAnalyzerResult(signals);
    }

    @Override
    public STCAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private BigDecimal extractOversoldLevel(STCAnalyzerRequest request) {
        return ofNullable(request.getOversoldLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(25));
    }

    private BigDecimal extractOverboughtLevel(STCAnalyzerRequest request) {
        return ofNullable(request.getOverboughtLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(75));
    }

    private Signal[] findSignals() {
        Signal[] oversoldSignals = findOversoldSignals();
        Signal[] overboughtSignals = findOverboughtSignals();
        return mergeSignals(oversoldSignals, overboughtSignals);
    }

    private Signal[] findOversoldSignals() {
        return new StaticLineCrossAnalyzer(indicatorValues, oversoldLevel)
                .withRemovingFalsePositive(SELL)
                .analyze();
    }

    private Signal[] findOverboughtSignals() {
        return new StaticLineCrossAnalyzer(indicatorValues, overboughtLevel)
                .withRemovingFalsePositive(BUY)
                .analyze();
    }

    private void buildSTCAnalyzerResult(Signal[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new STCAnalyzerResult(indicatorResults[idx].getTime(), signals[idx]))
                .toArray(STCAnalyzerResult[]::new);
    }

}
