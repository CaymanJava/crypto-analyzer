package pro.crypto.analyzer.atrb;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.atrb.ATRBRequest;
import pro.crypto.indicator.atrb.AverageTrueRangeBands;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ATRBAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testAverageTrueRangeBandsAnalyzer() {
        IndicatorResult[] indicatorResults = new AverageTrueRangeBands(buildIndicatorRequest()).getResult();
        ATRBAnalyzerResult[] result = new ATRBAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertFalse(result[0].isCrossUpperBand());
        assertFalse(result[0].isCrossLowerBand());
        assertFalse(result[0].isCrossMiddleBand());
        assertEquals(result[4].getTime(), of(2018, 3, 1, 0, 0));
        assertFalse(result[4].isCrossUpperBand());
        assertFalse(result[4].isCrossLowerBand());
        assertTrue(result[4].isCrossMiddleBand());
        assertEquals(result[10].getTime(), of(2018, 3, 7, 0, 0));
        assertTrue(result[10].isCrossUpperBand());
        assertFalse(result[10].isCrossLowerBand());
        assertTrue(result[10].isCrossMiddleBand());
        assertEquals(result[16].getTime(), of(2018, 3, 13, 0, 0));
        assertFalse(result[16].isCrossUpperBand());
        assertTrue(result[16].isCrossLowerBand());
        assertTrue(result[16].isCrossMiddleBand());
        assertEquals(result[18].getTime(), of(2018, 3, 15, 0, 0));
        assertTrue(result[18].isCrossUpperBand());
        assertFalse(result[18].isCrossLowerBand());
        assertTrue(result[18].isCrossMiddleBand());
        assertEquals(result[27].getTime(), of(2018, 3, 24, 0, 0));
        assertFalse(result[27].isCrossUpperBand());
        assertTrue(result[27].isCrossLowerBand());
        assertTrue(result[27].isCrossMiddleBand());
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertFalse(result[43].isCrossUpperBand());
        assertTrue(result[43].isCrossLowerBand());
        assertTrue(result[43].isCrossMiddleBand());
        assertEquals(result[63].getTime(), of(2018, 4, 29, 0, 0));
        assertTrue(result[63].isCrossUpperBand());
        assertFalse(result[63].isCrossLowerBand());
        assertTrue(result[63].isCrossMiddleBand());
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertFalse(result[72].isCrossUpperBand());
        assertFalse(result[72].isCrossLowerBand());
        assertTrue(result[72].isCrossMiddleBand());
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return ATRBRequest.builder()
                .originalData(originalData)
                .period(5)
                .shift(1)
                .priceType(CLOSE)
                .build();
    }

}