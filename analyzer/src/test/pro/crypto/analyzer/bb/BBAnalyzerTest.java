package pro.crypto.analyzer.bb;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.bb.BBRequest;
import pro.crypto.indicator.bb.BollingerBands;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class BBAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testBollingerBandsAnalyzer() {
        IndicatorResult[] indicatorResults = new BollingerBands(buildIndicatorRequest()).getResult();
        BBAnalyzerResult[] result = new BBAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertFalse(result[0].isCrossUpperBand());
        assertFalse(result[0].isCrossLowerBand());
        assertFalse(result[0].isCrossMiddleBand());
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertFalse(result[19].isCrossUpperBand());
        assertTrue(result[19].isCrossLowerBand());
        assertFalse(result[19].isCrossMiddleBand());
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertFalse(result[28].isCrossUpperBand());
        assertFalse(result[28].isCrossLowerBand());
        assertTrue(result[28].isCrossMiddleBand());
        assertEquals(result[30].getTime(), of(2018, 3, 27, 0, 0));
        assertTrue(result[30].isCrossUpperBand());
        assertFalse(result[30].isCrossLowerBand());
        assertFalse(result[30].isCrossMiddleBand());
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertFalse(result[49].isCrossUpperBand());
        assertFalse(result[49].isCrossLowerBand());
        assertTrue(result[49].isCrossMiddleBand());
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertTrue(result[57].isCrossUpperBand());
        assertFalse(result[57].isCrossLowerBand());
        assertFalse(result[57].isCrossMiddleBand());
        assertEquals(result[63].getTime(), of(2018, 4, 29, 0, 0));
        assertTrue(result[63].isCrossUpperBand());
        assertFalse(result[63].isCrossLowerBand());
        assertFalse(result[63].isCrossMiddleBand());
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertFalse(result[72].isCrossUpperBand());
        assertFalse(result[72].isCrossLowerBand());
        assertFalse(result[72].isCrossMiddleBand());
    }

    private IndicatorRequest buildIndicatorRequest() {
        return BBRequest.builder()
                .originalData(originalData)
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}