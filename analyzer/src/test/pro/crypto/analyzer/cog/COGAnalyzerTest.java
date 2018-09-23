package pro.crypto.analyzer.cog;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.cog.COGRequest;
import pro.crypto.indicator.cog.CenterOfGravity;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class COGAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testCenterOfGravityAnalyzer() {
        IndicatorResult[] indicatorResults = new CenterOfGravity(buildIndicatorRequest()).getResult();
        COGAnalyzerResult[] result = new COGAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[24].getTime(), of(2018, 3, 21, 0, 0));
        assertEquals(result[24].getSignal(), SELL);
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getSignal(), BUY);
        assertEquals(result[37].getTime(), of(2018, 4, 3, 0, 0));
        assertEquals(result[37].getSignal(), SELL);
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getSignal(), BUY);
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getSignal(), SELL);
        assertEquals(result[56].getTime(), of(2018, 4, 22, 0, 0));
        assertEquals(result[56].getSignal(), BUY);
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getSignal(), SELL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return COGRequest.builder()
                .originalData(originalData)
                .period(10)
                .priceType(CLOSE)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .signalLinePeriod(10)
                .build();
    }

}
