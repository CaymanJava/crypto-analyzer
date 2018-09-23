package pro.crypto.analyzer.bbw;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.bbw.BBWRequest;
import pro.crypto.indicator.bbw.BollingerBandsWidth;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class BBWAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testBollingerBandsWidthAnalyzer() {
        IndicatorResult[] indicatorResults = new BollingerBandsWidth(buildIndicatorRequest()).getResult();
        BBWAnalyzerResult[] result = new BBWAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertFalse(result[0].isStartTrend());
        assertFalse(result[0].isTrend());
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertFalse(result[19].isStartTrend());
        assertTrue(result[19].isTrend());
        assertEquals(result[50].getTime(), of(2018, 4, 16, 0, 0));
        assertFalse(result[50].isStartTrend());
        assertTrue(result[50].isTrend());
        assertEquals(result[51].getTime(), of(2018, 4, 17, 0, 0));
        assertFalse(result[51].isStartTrend());
        assertFalse(result[51].isTrend());
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertFalse(result[57].isStartTrend());
        assertFalse(result[57].isTrend());
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertTrue(result[58].isStartTrend());
        assertTrue(result[58].isTrend());
        assertEquals(result[59].getTime(), of(2018, 4, 25, 0, 0));
        assertFalse(result[59].isStartTrend());
        assertTrue(result[59].isTrend());
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertFalse(result[68].isStartTrend());
        assertTrue(result[68].isTrend());
        assertEquals(result[69].getTime(), of(2018, 5, 5, 0, 0));
        assertFalse(result[69].isStartTrend());
        assertFalse(result[69].isTrend());
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertFalse(result[72].isStartTrend());
        assertFalse(result[72].isTrend());
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return BBWRequest.builder()
                .originalData(originalData)
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build();
    }

}
