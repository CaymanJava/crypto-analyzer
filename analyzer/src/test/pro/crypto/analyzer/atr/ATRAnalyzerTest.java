package pro.crypto.analyzer.atr;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.atr.ATRRequest;
import pro.crypto.indicator.atr.AverageTrueRange;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;

public class ATRAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAverageTrueRangeAnalyzer() {
        IndicatorResult[] indicatorResults = new AverageTrueRange(buildIndicatorRequest()).getResult();
        ATRAnalyzerResult[] result = new ATRAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertFalse(result[0].isStartTrend());
        assertFalse(result[0].isTrend());
        assertEquals(result[9].getTime(), of(2018, 3, 6, 0, 0));
        assertFalse(result[9].isStartTrend());
        assertFalse(result[9].isTrend());
        assertEquals(result[18].getTime(), of(2018, 3, 15, 0, 0));
        assertFalse(result[18].isStartTrend());
        assertTrue(result[18].isTrend());
        assertEquals(result[27].getTime(), of(2018, 3, 24, 0, 0));
        assertTrue(result[27].isStartTrend());
        assertTrue(result[27].isTrend());
        assertEquals(result[37].getTime(), of(2018, 4, 3, 0, 0));
        assertFalse(result[37].isStartTrend());
        assertFalse(result[37].isTrend());
        assertEquals(result[44].getTime(), of(2018, 4, 10, 0, 0));
        assertTrue(result[44].isStartTrend());
        assertTrue(result[44].isTrend());
        assertEquals(result[61].getTime(), of(2018, 4, 27, 0, 0));
        assertTrue(result[61].isStartTrend());
        assertTrue(result[61].isTrend());
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertFalse(result[72].isStartTrend());
        assertFalse(result[72].isTrend());
    }

    private IndicatorRequest buildIndicatorRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(10)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}