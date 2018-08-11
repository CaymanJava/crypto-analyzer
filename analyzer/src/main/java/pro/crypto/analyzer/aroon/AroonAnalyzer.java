package pro.crypto.analyzer.aroon;

import pro.crypto.indicator.aroon.AroonResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class AroonAnalyzer implements Analyzer<AroonAnalyzerResult> {

    private final Tick[] originalData;
    private final AroonResult[] indicatorResults;

    private AroonAnalyzerResult[] result;

    public AroonAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (AroonResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        TrendStrength[] trends = findTrendsAndStrength();
        buildAroonUpAnalyzerResult(trends);
    }

    @Override
    public AroonAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private TrendStrength[] findTrendsAndStrength() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::findTrend)
                .toArray(TrendStrength[]::new);
    }

    private TrendStrength findTrend(int currentIndex) {
        return isPossibleDefineTrend(currentIndex)
                ? defineTrend(currentIndex)
                : new TrendStrength(Trend.UNDEFINED, Strength.UNDEFINED);
    }

    private boolean isPossibleDefineTrend(int currentIndex) {
        return nonNull(indicatorResults[currentIndex].getAroonDown())
                && nonNull(indicatorResults[currentIndex].getAroonUp());
    }

    private TrendStrength defineTrend(int currentIndex) {
        if (hasIndicatorExtremeValues(currentIndex)) {
            return defineExtremeValueTrend(currentIndex);
        }

        if (isUpTrend(currentIndex)) {
            return defineUpTrend(currentIndex);
        }

        if (isDownTrend(currentIndex)) {
            return defineDownTrend(currentIndex);
        }

        if (isIntersection(currentIndex)) {
            return defineIntersection(currentIndex);
        }

        return new TrendStrength(Trend.CONSOLIDATION, Strength.NORMAL);
    }

    private boolean hasIndicatorExtremeValues(int currentIndex) {
        return indicatorResults[currentIndex].getAroonUp().compareTo(indicatorResults[currentIndex].getAroonDown()) != 0
                && isExtremeValue(indicatorResults[currentIndex].getAroonUp())
                || isExtremeValue(indicatorResults[currentIndex].getAroonDown());
    }

    private TrendStrength defineExtremeValueTrend(int currentIndex) {
        if (isExtremeValue(indicatorResults[currentIndex].getAroonUp())) {
            return new TrendStrength(Trend.UPTREND, Strength.STRONG);
        }
        return new TrendStrength(Trend.DOWNTREND, Strength.STRONG);
    }

    private boolean isExtremeValue(BigDecimal aroonValue) {
        return aroonValue.compareTo(new BigDecimal(100)) == 0;
    }

    private boolean isUpTrend(int currentIndex) {
        return indicatorResults[currentIndex].getAroonUp().compareTo(new BigDecimal(50)) > 0
                && indicatorResults[currentIndex].getAroonDown().compareTo(new BigDecimal(50)) <= 0;
    }

    private TrendStrength defineUpTrend(int currentIndex) {
        if (isStrongUpTrend(currentIndex)) {
            return new TrendStrength(Trend.UPTREND, Strength.STRONG);
        }

        if (isNormalUpTrend(currentIndex)) {
            return new TrendStrength(Trend.UPTREND, Strength.NORMAL);
        }

        return new TrendStrength(Trend.UPTREND, Strength.WEAK);
    }

    private boolean isStrongUpTrend(int currentIndex) {
        return indicatorResults[currentIndex].getAroonUp().compareTo(new BigDecimal(70)) >= 0
                && indicatorResults[currentIndex].getAroonDown().compareTo(new BigDecimal(30)) <= 0;
    }

    private boolean isNormalUpTrend(int currentIndex) {
        return indicatorResults[currentIndex].getAroonUp().compareTo(new BigDecimal(50)) >= 0;
    }

    private boolean isDownTrend(int currentIndex) {
        return indicatorResults[currentIndex].getAroonUp().compareTo(new BigDecimal(50)) <= 0
                && indicatorResults[currentIndex].getAroonDown().compareTo(new BigDecimal(50)) > 0;
    }

    private TrendStrength defineDownTrend(int currentIndex) {
        if (isStrongDownTrend(currentIndex)) {
            return new TrendStrength(Trend.DOWNTREND, Strength.STRONG);
        }

        if (isNormalDownTrend(currentIndex)) {
            return new TrendStrength(Trend.DOWNTREND, Strength.NORMAL);
        }

        return new TrendStrength(Trend.DOWNTREND, Strength.WEAK);
    }

    private boolean isStrongDownTrend(int currentIndex) {
        return indicatorResults[currentIndex].getAroonDown().compareTo(new BigDecimal(70)) >= 0
                && indicatorResults[currentIndex].getAroonUp().compareTo(new BigDecimal(30)) <= 0;
    }

    private boolean isNormalDownTrend(int currentIndex) {
        return indicatorResults[currentIndex].getAroonDown().compareTo(new BigDecimal(50)) >= 0;
    }

    private boolean isIntersection(int currentIndex) {
        return isPossibleDefineIntersection(currentIndex) && isIntersectionExist(currentIndex);
    }

    private boolean isPossibleDefineIntersection(int currentIndex) {
        return currentIndex > 0
                && nonNull(indicatorResults[currentIndex - 1].getAroonUp())
                && nonNull(indicatorResults[currentIndex - 1].getAroonDown())
                && nonNull(indicatorResults[currentIndex].getAroonUp())
                && nonNull(indicatorResults[currentIndex].getAroonDown());
    }

    private boolean isIntersectionExist(int currentIndex) {
        return indicatorResults[currentIndex - 1].getAroonUp().compareTo(indicatorResults[currentIndex - 1].getAroonDown())
                != indicatorResults[currentIndex].getAroonUp().compareTo(indicatorResults[currentIndex].getAroonDown());
    }

    private TrendStrength defineIntersection(int currentIndex) {
        if (isUpTrendIntersection(currentIndex)) {
            return new TrendStrength(Trend.UPTREND, Strength.WEAK);
        }
        return new TrendStrength(Trend.DOWNTREND, Strength.WEAK);
    }

    private boolean isUpTrendIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getAroonUp().compareTo(indicatorResults[currentIndex - 1].getAroonDown()) < 0
                && indicatorResults[currentIndex].getAroonUp().compareTo(indicatorResults[currentIndex].getAroonDown()) >= 0;
    }

    private void buildAroonUpAnalyzerResult(TrendStrength[] trends) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> buildAroonUpAnalyzerResult(trends[idx], idx))
                .toArray(AroonAnalyzerResult[]::new);
    }

    private AroonAnalyzerResult buildAroonUpAnalyzerResult(TrendStrength trend, int currentIndex) {
        return new AroonAnalyzerResult(
                originalData[currentIndex].getTickTime(), null,
                indicatorResults[currentIndex].getAroonOscillator(), originalData[currentIndex].getClose(),
                trend.getTrend(), trend.getStrength()
        );
    }

}
