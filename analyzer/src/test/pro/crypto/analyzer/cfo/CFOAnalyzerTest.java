package pro.crypto.analyzer.cfo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.cfo.CFORequest;
import pro.crypto.indicator.cfo.ChandeForecastOscillator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class CFOAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testChandeForecastOscillatorAnalyzer() {
        IndicatorResult[] indicatorResults = new ChandeForecastOscillator(buildIndicatorRequest()).getResult();
        CFOAnalyzerResult[] result = new CFOAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[4].getTime(), of(2018, 3, 1, 0, 0));
        assertEquals(result[4].getSignal(), NEUTRAL);
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getSignal(), SELL);
        assertEquals(result[16].getTime(), of(2018, 3, 13, 0, 0));
        assertEquals(result[16].getSignal(), BUY);
        assertEquals(result[40].getTime(), of(2018, 4, 6, 0, 0));
        assertEquals(result[40].getSignal(), SELL);
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getSignal(), BUY);
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getSignal(), SELL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return CFORequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .period(5)
                .movingAveragePeriod(10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

}
