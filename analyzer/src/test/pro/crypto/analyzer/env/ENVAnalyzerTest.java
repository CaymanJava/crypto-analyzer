package pro.crypto.analyzer.env;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.env.ENVRequest;
import pro.crypto.indicator.env.MovingAverageEnvelopes;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.SignalStrength;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.*;

public class ENVAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testMovingAverageEnvelopesAnalyzer() {
        IndicatorResult[] indicatorResults = new MovingAverageEnvelopes(buildIndicatorRequest()).getResult();
        ENVAnalyzerResult[] result = new ENVAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertFalse(result[0].isCrossUpperBand());
        assertFalse(result[0].isCrossLowerBand());
        assertFalse(result[0].isCrossMiddleBand());
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertFalse(result[19].isCrossUpperBand());
        assertTrue(result[19].isCrossLowerBand());
        assertFalse(result[19].isCrossMiddleBand());
        assertEquals(result[19].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[25].getTime(), of(2018, 3, 22, 0, 0));
        assertFalse(result[25].isCrossUpperBand());
        assertTrue(result[25].isCrossLowerBand());
        assertFalse(result[25].isCrossMiddleBand());
        assertEquals(result[25].getSignalStrength(), new SignalStrength(BUY, WEAK));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertFalse(result[28].isCrossUpperBand());
        assertFalse(result[28].isCrossLowerBand());
        assertTrue(result[28].isCrossMiddleBand());
        assertEquals(result[28].getSignalStrength(), new SignalStrength(BUY, NORMAL));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertFalse(result[31].isCrossUpperBand());
        assertFalse(result[31].isCrossLowerBand());
        assertFalse(result[31].isCrossMiddleBand());
        assertEquals(result[31].getSignalStrength(), new SignalStrength(SELL, WEAK));
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertFalse(result[49].isCrossUpperBand());
        assertFalse(result[49].isCrossLowerBand());
        assertTrue(result[49].isCrossMiddleBand());
        assertEquals(result[49].getSignalStrength(), new SignalStrength(SELL, NORMAL));
        assertEquals(result[50].getTime(), of(2018, 4, 16, 0, 0));
        assertFalse(result[50].isCrossUpperBand());
        assertFalse(result[50].isCrossLowerBand());
        assertTrue(result[50].isCrossMiddleBand());
        assertEquals(result[50].getSignalStrength(), new SignalStrength(BUY, NORMAL));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertFalse(result[68].isCrossUpperBand());
        assertFalse(result[68].isCrossLowerBand());
        assertFalse(result[68].isCrossMiddleBand());
        assertEquals(result[68].getSignalStrength(), new SignalStrength(SELL, NORMAL));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertFalse(result[72].isCrossUpperBand());
        assertFalse(result[72].isCrossLowerBand());
        assertFalse(result[72].isCrossMiddleBand());
        assertEquals(result[72].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return ENVRequest.builder()
                .originalData(originalData)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .indentationPercentage(7.0)
                .build();
    }

}
