package pro.crypto.analyzer.st;

import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.PriceVolumeExtractor;
import pro.crypto.indicator.st.STResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;
import pro.crypto.model.Trend;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Trend.DOWNTREND;
import static pro.crypto.model.Trend.UNDEFINED;
import static pro.crypto.model.Trend.UPTREND;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class STAnalyzer implements Analyzer<STAnalyzerResult> {

    private final Tick[] originalData;
    private final STResult[] indicatorResults;

    private BigDecimal[] indicatorValues;
    private BigDecimal[] closePrices;
    private STAnalyzerResult[] result;

    public STAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (STResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        extractIndicatorValues();
        extractClosePrices();
        Signal[] signals = findSignals();
        Trend[] trends = findTrends();
        buildSTAnalyzerResult(signals, trends);
    }

    @Override
    public STAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void extractIndicatorValues() {
        indicatorValues = IndicatorResultExtractor.extractIndicatorValues(indicatorResults);
    }

    private void extractClosePrices() {
        closePrices = PriceVolumeExtractor.extractPrices(originalData, CLOSE);
    }

    private Signal[] findSignals() {
        return new DynamicLineCrossAnalyzer(closePrices, indicatorValues).analyze();
    }

    private Trend[] findTrends() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::findTrend)
                .toArray(Trend[]::new);
    }

    private Trend findTrend(int currentIndex) {
        return isPossibleDefineTrend(currentIndex)
                ? defineTrend(currentIndex)
                : UNDEFINED;
    }

    private boolean isPossibleDefineTrend(int currentIndex) {
        return nonNull(indicatorValues[currentIndex]);
    }

    private Trend defineTrend(int currentIndex) {
        return indicatorValues[currentIndex].compareTo(closePrices[currentIndex]) > 0
                ? DOWNTREND
                : UPTREND;
    }

    private void buildSTAnalyzerResult(Signal[] signals, Trend[] trends) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new STAnalyzerResult(indicatorResults[idx].getTime(), signals[idx], trends[idx]))
                .toArray(STAnalyzerResult[]::new);
    }

}
