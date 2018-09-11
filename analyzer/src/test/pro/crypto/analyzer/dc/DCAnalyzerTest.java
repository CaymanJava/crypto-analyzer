package pro.crypto.analyzer.dc;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.dc.DCRequest;
import pro.crypto.indicator.dc.DonchianChannel;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;

public class DCAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testDonchianChannelAnalyzer() {
        IndicatorResult[] indicatorResults = new DonchianChannel(buildIndicatorRequest()).getResult();
        DCAnalyzerResult[] result = new DCAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertFalse(result[0].isCrossUpperBand());
        assertFalse(result[0].isCrossLowerBand());
        assertFalse(result[0].isCrossMiddleBand());
        assertEquals(result[23].getTime(), of(2018, 3, 20, 0, 0));
        assertFalse(result[23].isCrossUpperBand());
        assertTrue(result[23].isCrossLowerBand());
        assertFalse(result[23].isCrossMiddleBand());
        assertEquals(result[27].getTime(), of(2018, 3, 24, 0, 0));
        assertFalse(result[27].isCrossUpperBand());
        assertTrue(result[27].isCrossLowerBand());
        assertFalse(result[27].isCrossMiddleBand());
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertFalse(result[28].isCrossUpperBand());
        assertFalse(result[28].isCrossLowerBand());
        assertTrue(result[28].isCrossMiddleBand());
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertTrue(result[32].isCrossUpperBand());
        assertFalse(result[32].isCrossLowerBand());
        assertFalse(result[32].isCrossMiddleBand());
        assertEquals(result[65].getTime(), of(2018, 5, 1, 0, 0));
        assertFalse(result[65].isCrossUpperBand());
        assertFalse(result[65].isCrossLowerBand());
        assertTrue(result[65].isCrossMiddleBand());
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertFalse(result[72].isCrossUpperBand());
        assertTrue(result[72].isCrossLowerBand());
        assertFalse(result[72].isCrossMiddleBand());
    }

    private IndicatorRequest buildIndicatorRequest() {
        return DCRequest.builder()
                .originalData(originalData)
                .highPeriod(20)
                .lowPeriod(20)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}
