package pro.crypto.analyzer.chop;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.chop.CHOPRequest;
import pro.crypto.indicator.chop.ChoppinessIndex;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;

public class CHOPAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testChoppinessIndexAnalyzer() {
        IndicatorResult[] indicatorResults = new ChoppinessIndex(buildIndicatorRequest()).getResult();
        CHOPAnalyzerResult[] result = new CHOPAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertFalse(result[0].isTrend());
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertTrue(result[13].isTrend());
        assertEquals(result[14].getTime(), of(2018, 3, 11, 0, 0));
        assertFalse(result[14].isTrend());
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertTrue(result[20].isTrend());
        assertEquals(result[35].getTime(), of(2018, 4, 1, 0, 0));
        assertFalse(result[35].isTrend());
        assertEquals(result[41].getTime(), of(2018, 4, 7, 0, 0));
        assertTrue(result[41].isTrend());
        assertEquals(result[63].getTime(), of(2018, 4, 29, 0, 0));
        assertFalse(result[63].isTrend());
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertTrue(result[72].isTrend());
    }

    private IndicatorRequest buildIndicatorRequest() {
        return CHOPRequest.builder()
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