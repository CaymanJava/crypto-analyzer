package pro.crypto.analyzer.bbw;

import pro.crypto.helper.IncreasedQualifier;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.bbw.BBWResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class BBWAnalyzer implements Analyzer<BBWAnalyzerResult> {

    private final BBWResult[] indicatorResults;

    private BBWAnalyzerResult[] result;

    public BBWAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (BBWResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        BigDecimal extremelyLowValue = calculateExtremelyLowLevel();
        buildBBAnalyzerResult(extremelyLowValue);
    }

    @Override
    public BBWAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal calculateExtremelyLowLevel() {
        return MathHelper.average(findCavities());
    }

    private BigDecimal[] findCavities() {
        Boolean[] increased = IncreasedQualifier.define(IndicatorResultExtractor.extractIndicatorValues(indicatorResults));
        return IntStream.range(1, increased.length)
                .filter(idx -> isPreviousValueCavity(increased[idx], increased[idx - 1]))
                .map(idx -> idx - 1)
                .mapToObj(idx -> indicatorResults[idx].getIndicatorValue())
                .toArray(BigDecimal[]::new);
    }

    private boolean isPreviousValueCavity(Boolean currentIncreased, Boolean previousIncreased) {
        return isPossibleDefineCavity(currentIncreased, previousIncreased) && (currentIncreased && !previousIncreased);
    }

    private boolean isPossibleDefineCavity(Boolean currentIncreased, Boolean previousIncreased) {
        return nonNull(currentIncreased) && nonNull(previousIncreased);
    }

    private void buildBBAnalyzerResult(BigDecimal extremelyLowValue) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> buildBBAnalyzerResult(extremelyLowValue, idx))
                .toArray(BBWAnalyzerResult[]::new);
    }

    private BBWAnalyzerResult buildBBAnalyzerResult(BigDecimal extremelyLowValue, int currentIndex) {
        return new BBWAnalyzerResult(
                indicatorResults[currentIndex].getTime(),
                defineStartTrend(extremelyLowValue, currentIndex),
                isTrendExist(extremelyLowValue, currentIndex)
        );
    }

    private Boolean defineStartTrend(BigDecimal extremelyLowValue, int currentIndex) {
        return isPossibleToDefineStartTrend(currentIndex)
                ? isStartTrend(extremelyLowValue, currentIndex)
                : false;
    }

    private boolean isPossibleToDefineStartTrend(int currentIndex) {
        return currentIndex > 0
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getIndicatorValue());
    }

    private Boolean isStartTrend(BigDecimal extremelyLowValue, int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(extremelyLowValue) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(extremelyLowValue) >= 0;
    }

    private Boolean isTrendExist(BigDecimal extremelyLowValue, int currentIndex) {
        return nonNull(indicatorResults[currentIndex].getIndicatorValue())
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(extremelyLowValue) >= 0;
    }

}
