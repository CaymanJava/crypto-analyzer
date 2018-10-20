package pro.crypto.analyzer.aroon;

import pro.crypto.indicator.aroon.AroonResult;
import pro.crypto.model.*;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

public class AroonAnalyzer implements Analyzer<AroonAnalyzerResult> {

    private final AroonResult[] indicatorResults;
    private final BigDecimal weakTrendLine;
    private final BigDecimal normalTrendLine;
    private final BigDecimal strongTrendLine;

    private AroonAnalyzerResult[] result;

    public AroonAnalyzer(AnalyzerRequest analyzerRequest) {
        AroonAnalyzerRequest request = (AroonAnalyzerRequest) analyzerRequest;
        this.indicatorResults = (AroonResult[]) request.getIndicatorResults();
        this.weakTrendLine = extractWeakTrendLine(request);
        this.normalTrendLine = extractNormalTrendLine(request);
        this.strongTrendLine = extractStrongTrendLine(request);
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

    private BigDecimal extractWeakTrendLine(AroonAnalyzerRequest request) {
        return ofNullable(request.getWeakTrendLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(30));
    }

    private BigDecimal extractNormalTrendLine(AroonAnalyzerRequest request) {
        return ofNullable(request.getNormalTrendLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(50));
    }

    private BigDecimal extractStrongTrendLine(AroonAnalyzerRequest request) {
        return ofNullable(request.getStrongTrendLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(70));
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
        return indicatorResults[currentIndex].getAroonUp().compareTo(normalTrendLine) > 0
                && indicatorResults[currentIndex].getAroonDown().compareTo(normalTrendLine) <= 0;
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
        return indicatorResults[currentIndex].getAroonUp().compareTo(strongTrendLine) >= 0
                && indicatorResults[currentIndex].getAroonDown().compareTo(weakTrendLine) <= 0;
    }

    private boolean isNormalUpTrend(int currentIndex) {
        return indicatorResults[currentIndex].getAroonUp().compareTo(normalTrendLine) >= 0;
    }

    private boolean isDownTrend(int currentIndex) {
        return indicatorResults[currentIndex].getAroonUp().compareTo(normalTrendLine) <= 0
                && indicatorResults[currentIndex].getAroonDown().compareTo(normalTrendLine) > 0;
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
        return indicatorResults[currentIndex].getAroonDown().compareTo(strongTrendLine) >= 0
                && indicatorResults[currentIndex].getAroonUp().compareTo(weakTrendLine) <= 0;
    }

    private boolean isNormalDownTrend(int currentIndex) {
        return indicatorResults[currentIndex].getAroonDown().compareTo(normalTrendLine) >= 0;
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
        return new AroonAnalyzerResult(indicatorResults[currentIndex].getTime(), new TrendStrength(trend.getTrend(), trend.getStrength()));
    }

}
