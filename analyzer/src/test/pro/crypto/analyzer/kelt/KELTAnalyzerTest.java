package pro.crypto.analyzer.kelt;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.kelt.KELTRequest;
import pro.crypto.indicator.kelt.KeltnerChannel;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.SignalStrength;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class KELTAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testKeltnerChannelAnalyzer() {
        IndicatorResult[] indicatorResults = new KeltnerChannel(buildIndicatorRequest()).getResult();
        KELTAnalyzerResult[] result = new KELTAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertFalse(result[0].isCrossUpperBand());
        assertFalse(result[0].isCrossLowerBand());
        assertFalse(result[0].isCrossMiddleBand());
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertFalse(result[20].isCrossUpperBand());
        assertTrue(result[20].isCrossLowerBand());
        assertFalse(result[20].isCrossMiddleBand());
        assertEquals(result[20].getSignalStrength(), new SignalStrength(BUY, WEAK));
        assertEquals(result[23].getTime(), of(2018, 3, 20, 0, 0));
        assertFalse(result[23].isCrossUpperBand());
        assertTrue(result[23].isCrossLowerBand());
        assertFalse(result[23].isCrossMiddleBand());
        assertEquals(result[23].getSignalStrength(), new SignalStrength(SELL, WEAK));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertFalse(result[28].isCrossUpperBand());
        assertFalse(result[28].isCrossLowerBand());
        assertTrue(result[28].isCrossMiddleBand());
        assertEquals(result[28].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[47].getTime(), of(2018, 4, 13, 0, 0));
        assertTrue(result[47].isCrossUpperBand());
        assertFalse(result[47].isCrossLowerBand());
        assertFalse(result[47].isCrossMiddleBand());
        assertEquals(result[47].getSignalStrength(), new SignalStrength(SELL, WEAK));
        assertEquals(result[50].getTime(), of(2018, 4, 16, 0, 0));
        assertFalse(result[50].isCrossUpperBand());
        assertFalse(result[50].isCrossLowerBand());
        assertFalse(result[50].isCrossMiddleBand());
        assertEquals(result[50].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertFalse(result[68].isCrossUpperBand());
        assertFalse(result[68].isCrossLowerBand());
        assertFalse(result[68].isCrossMiddleBand());
        assertEquals(result[68].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertFalse(result[72].isCrossUpperBand());
        assertFalse(result[72].isCrossLowerBand());
        assertFalse(result[72].isCrossMiddleBand());
        assertEquals(result[72].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return KELTRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build();
    }

}
