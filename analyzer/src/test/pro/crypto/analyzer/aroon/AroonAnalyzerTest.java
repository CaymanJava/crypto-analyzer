package pro.crypto.analyzer.aroon;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.aroon.AroonRequest;
import pro.crypto.indicator.aroon.AroonUpDown;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AroonAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAroonAnalyzer() {
        IndicatorResult[] indicatorResults = new AroonUpDown(buildIndicatorRequest()).getResult();
        AroonAnalyzerResult[] result = new AroonAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getTrendStrength(), new TrendStrength(Trend.UNDEFINED, Strength.UNDEFINED));
        assertEquals(result[14].getTime(), of(2018, 3, 11, 0, 0));
        assertEquals(result[14].getTrendStrength(), new TrendStrength(Trend.DOWNTREND, Strength.STRONG));
        assertEquals(result[30].getTime(), of(2018, 3, 27, 0, 0));
        assertEquals(result[30].getTrendStrength(), new TrendStrength(Trend.UPTREND, Strength.STRONG));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getTrendStrength(), new TrendStrength(Trend.CONSOLIDATION, Strength.NORMAL));
        assertEquals(result[35].getTime(), of(2018, 4, 1, 0, 0));
        assertEquals(result[35].getTrendStrength(), new TrendStrength(Trend.UPTREND, Strength.NORMAL));
        assertEquals(result[50].getTime(), of(2018, 4, 16, 0, 0));
        assertEquals(result[50].getTrendStrength(), new TrendStrength(Trend.UPTREND, Strength.NORMAL));
        assertEquals(result[51].getTime(), of(2018, 4, 17, 0, 0));
        assertEquals(result[51].getTrendStrength(), new TrendStrength(Trend.CONSOLIDATION, Strength.NORMAL));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getTrendStrength(), new TrendStrength(Trend.DOWNTREND, Strength.STRONG));
        assertEquals(result[69].getTime(), of(2018, 5, 5, 0, 0));
        assertEquals(result[69].getTrendStrength(), new TrendStrength(Trend.CONSOLIDATION, Strength.NORMAL));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getTrendStrength(), new TrendStrength(Trend.DOWNTREND, Strength.STRONG));
    }

    private IndicatorRequest buildIndicatorRequest() {
        return AroonRequest.builder()
                .originalData(originalData)
                .period(14)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}