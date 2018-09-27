package pro.crypto.analyzer.hlb;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.hlb.HLBRequest;
import pro.crypto.indicator.hlb.HighLowBands;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class HLBAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testHighLowBandsAnalyzer() {
        IndicatorResult[] indicatorResults = new HighLowBands(buildIndicatorRequest()).getResult();
        HLBAnalyzerResult[] result = new HLBAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertFalse(result[0].isCrossUpperBand());
        assertFalse(result[0].isCrossLowerBand());
        assertFalse(result[0].isCrossMiddleBand());
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertFalse(result[19].isCrossUpperBand());
        assertTrue(result[19].isCrossLowerBand());
        assertFalse(result[19].isCrossMiddleBand());
        assertEquals(result[27].getTime(), of(2018, 3, 24, 0, 0));
        assertFalse(result[27].isCrossUpperBand());
        assertTrue(result[27].isCrossLowerBand());
        assertFalse(result[27].isCrossMiddleBand());
        assertEquals(result[40].getTime(), of(2018, 4, 6, 0, 0));
        assertFalse(result[40].isCrossUpperBand());
        assertFalse(result[40].isCrossLowerBand());
        assertTrue(result[40].isCrossMiddleBand());
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertTrue(result[45].isCrossUpperBand());
        assertFalse(result[45].isCrossLowerBand());
        assertFalse(result[45].isCrossMiddleBand());
        assertEquals(result[50].getTime(), of(2018, 4, 16, 0, 0));
        assertFalse(result[50].isCrossUpperBand());
        assertFalse(result[50].isCrossLowerBand());
        assertTrue(result[50].isCrossMiddleBand());
        assertEquals(result[63].getTime(), of(2018, 4, 29, 0, 0));
        assertTrue(result[63].isCrossUpperBand());
        assertFalse(result[63].isCrossLowerBand());
        assertFalse(result[63].isCrossMiddleBand());
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertFalse(result[72].isCrossUpperBand());
        assertFalse(result[72].isCrossLowerBand());
        assertFalse(result[72].isCrossMiddleBand());
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return HLBRequest.builder()
                .originalData(originalData)
                .period(13)
                .shiftPercentage(5)
                .priceType(CLOSE)
                .build();
    }

}
