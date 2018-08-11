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
        assertEquals(result[0].getTrend(), Trend.UNDEFINED);
        assertEquals(result[0].getStrength(), Strength.UNDEFINED);
        assertEquals(result[14].getTrend(), Trend.DOWNTREND);
        assertEquals(result[14].getStrength(), Strength.STRONG);
        assertEquals(result[30].getTrend(), Trend.UPTREND);
        assertEquals(result[30].getStrength(), Strength.STRONG);
        assertEquals(result[31].getTrend(), Trend.CONSOLIDATION);
        assertEquals(result[31].getStrength(), Strength.NORMAL);
        assertEquals(result[35].getTrend(), Trend.UPTREND);
        assertEquals(result[35].getStrength(), Strength.NORMAL);
        assertEquals(result[50].getTrend(), Trend.UPTREND);
        assertEquals(result[50].getStrength(), Strength.NORMAL);
        assertEquals(result[51].getTrend(), Trend.CONSOLIDATION);
        assertEquals(result[51].getStrength(), Strength.NORMAL);
        assertEquals(result[68].getTrend(), Trend.DOWNTREND);
        assertEquals(result[68].getStrength(), Strength.STRONG);
        assertEquals(result[69].getTrend(), Trend.CONSOLIDATION);
        assertEquals(result[69].getStrength(), Strength.NORMAL);
        assertEquals(result[72].getTrend(), Trend.DOWNTREND);
        assertEquals(result[72].getStrength(), Strength.STRONG);
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