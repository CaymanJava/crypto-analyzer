package pro.crypto.analyzer.alligator;

import pro.crypto.indicator.alligator.AlligatorResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.AnalyzerResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.NEUTRAL;

public class AlligatorAnalyzer implements Analyzer<AlligatorAnalyzerResult> {

    private final Tick[] originalData;
    private final AlligatorResult[] indicatorResults;

    private int[] awakePeriods;
    private boolean[] trends;

    private AlligatorAnalyzerResult[] result;

    public AlligatorAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (AlligatorResult[]) request.getIndicatorResults();
        this.awakePeriods = new int[this.indicatorResults.length];
        this.trends = new boolean[this.indicatorResults.length];
    }

    @Override
    public void analyze() {
        defineAwakePeriodsSleeping();
        defineTrends();
        buildAlligatorsAnalyzerResults();
    }

    @Override
    public AlligatorAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void defineAwakePeriodsSleeping() {
        IntStream.range(0, indicatorResults.length)
                .forEach(idx -> awakePeriods[idx] = tryDefineAwakePeriods(idx));
    }

    private int tryDefineAwakePeriods(int currentIndex) {
        return isPossibleDefine(currentIndex)
                ? defineAwakePeriods(currentIndex)
                : 0;
    }

    private int defineAwakePeriods(int currentIndex) {
        return isLineMutualIntersectionExist(currentIndex) ? 0 : awakePeriods[currentIndex - 1] + 1;
    }

    private void defineTrends() {
        IntStream.range(0, indicatorResults.length)
                .forEach(idx -> trends[idx] = defineTrend(idx));
    }

    private boolean defineTrend(int currentIndex) {
        return isPossibleDefine(currentIndex) && !isIntersectionExist(currentIndex) && trendExist(currentIndex);
    }

    private boolean isPossibleDefine(int currentIndex) {
        return currentIndex != 0
                && nonNull(indicatorResults[currentIndex].getJawValue())
                && nonNull(indicatorResults[currentIndex].getLipsValue())
                && nonNull(indicatorResults[currentIndex].getTeethValue())
                && nonNull(indicatorResults[currentIndex - 1].getJawValue())
                && nonNull(indicatorResults[currentIndex - 1].getLipsValue())
                && nonNull(indicatorResults[currentIndex - 1].getTeethValue());
    }

    private boolean isIntersectionExist(int currentIndex) {
        return isLineMutualIntersectionExist(currentIndex) || linesCrossPrice(currentIndex);
    }

    private boolean isLineMutualIntersectionExist(int currentIndex) {
        return isJawAndTeethCross(currentIndex)
                || isJawAndLipsCross(currentIndex)
                || isTeethAndLipsCross(currentIndex);
    }

    private boolean isJawAndTeethCross(int currentIndex) {
        return indicatorResults[currentIndex - 1].getJawValue()
                .compareTo(indicatorResults[currentIndex - 1].getTeethValue())
                != indicatorResults[currentIndex].getJawValue()
                .compareTo(indicatorResults[currentIndex].getTeethValue());
    }

    private boolean isJawAndLipsCross(int currentIndex) {
        return indicatorResults[currentIndex - 1].getJawValue()
                .compareTo(indicatorResults[currentIndex - 1].getLipsValue())
                != indicatorResults[currentIndex].getJawValue()
                .compareTo(indicatorResults[currentIndex].getLipsValue());
    }

    private boolean isTeethAndLipsCross(int currentIndex) {
        return indicatorResults[currentIndex - 1].getTeethValue()
                .compareTo(indicatorResults[currentIndex - 1].getLipsValue())
                != indicatorResults[currentIndex].getTeethValue()
                .compareTo(indicatorResults[currentIndex].getLipsValue());
    }

    private boolean linesCrossPrice(int currentIndex) {
        return isJawAndPriceCross(currentIndex)
                || isLipsAndPriceCross(currentIndex)
                || isTeethAndPriceCross(currentIndex);
    }

    private boolean isJawAndPriceCross(int currentIndex) {
        return indicatorResults[currentIndex - 1].getJawValue()
                .compareTo(originalData[currentIndex - 1].getClose())
                != indicatorResults[currentIndex].getJawValue()
                .compareTo(originalData[currentIndex].getClose());
    }

    private boolean isLipsAndPriceCross(int currentIndex) {
        return indicatorResults[currentIndex - 1].getLipsValue()
                .compareTo(originalData[currentIndex - 1].getClose())
                != indicatorResults[currentIndex].getLipsValue()
                .compareTo(originalData[currentIndex].getClose());
    }

    private boolean isTeethAndPriceCross(int currentIndex) {
        return indicatorResults[currentIndex - 1].getTeethValue()
                .compareTo(originalData[currentIndex - 1].getClose())
                != indicatorResults[currentIndex].getTeethValue()
                .compareTo(originalData[currentIndex].getClose());
    }

    private boolean trendExist(int currentIndex) {
        return isJawLineFartherFromPrice(currentIndex)
                && isTeethLineBetweenJawAndLips(currentIndex)
                && lipsLineCloserToPrice(currentIndex);
    }

    private boolean isJawLineFartherFromPrice(int currentIndex) {
        BigDecimal jawPriceDistance = originalData[currentIndex].getClose().subtract(indicatorResults[currentIndex].getJawValue()).abs();
        BigDecimal teethPriceDistance = originalData[currentIndex].getClose().subtract(indicatorResults[currentIndex].getTeethValue()).abs();
        BigDecimal lipsPriceDistance = originalData[currentIndex].getClose().subtract(indicatorResults[currentIndex].getLipsValue()).abs();
        return jawPriceDistance.compareTo(teethPriceDistance) > 0 && jawPriceDistance.compareTo(lipsPriceDistance) > 0;
    }

    private boolean isTeethLineBetweenJawAndLips(int currentIndex) {
        return (indicatorResults[currentIndex].getJawValue().compareTo(indicatorResults[currentIndex].getTeethValue()) > 0
                && indicatorResults[currentIndex].getTeethValue().compareTo(indicatorResults[currentIndex].getLipsValue()) > 0)
                || (indicatorResults[currentIndex].getJawValue().compareTo(indicatorResults[currentIndex].getTeethValue()) < 0
                && indicatorResults[currentIndex].getTeethValue().compareTo(indicatorResults[currentIndex].getLipsValue()) < 0);
    }

    private boolean lipsLineCloserToPrice(int currentIndex) {
        BigDecimal jawPriceDistance = originalData[currentIndex].getClose().subtract(indicatorResults[currentIndex].getJawValue()).abs();
        BigDecimal teethPriceDistance = originalData[currentIndex].getClose().subtract(indicatorResults[currentIndex].getTeethValue()).abs();
        BigDecimal lipsPriceDistance = originalData[currentIndex].getClose().subtract(indicatorResults[currentIndex].getLipsValue()).abs();
        return lipsPriceDistance.compareTo(teethPriceDistance) < 0 && lipsPriceDistance.compareTo(jawPriceDistance) < 0;
    }

    private void buildAlligatorsAnalyzerResults() {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(this::buildAlligatorsAnalyzerResult)
                .toArray(AlligatorAnalyzerResult[]::new);
    }

    private AnalyzerResult buildAlligatorsAnalyzerResult(int currentIndex) {
        return AlligatorAnalyzerResult.builder()
                .time(originalData[currentIndex].getTickTime())
                .signal(NEUTRAL)
                .indicatorValue(null)
                .closePrice(originalData[currentIndex].getClose())
                .awakePeriods(awakePeriods[currentIndex])
                .trend(trends[currentIndex])
                .build();
    }

}
