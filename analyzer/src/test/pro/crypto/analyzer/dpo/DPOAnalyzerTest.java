package pro.crypto.analyzer.dpo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.dpo.DPORequest;
import pro.crypto.indicator.dpo.DetrendedPriceOscillator;
import pro.crypto.model.IndicatorResult;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class DPOAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testCenterOfGravityAnalyzerWithPeriodSeven() {
        DPORequest indicatorRequest = buildIndicatorRequest();
        indicatorRequest.setPeriod(7);
        indicatorRequest.setMovingAverageType(SIMPLE_MOVING_AVERAGE);
        IndicatorResult[] indicatorResults = new DetrendedPriceOscillator(indicatorRequest).getResult();
        DPOAnalyzerResult[] result = new DPOAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getSignal(), BUY);
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getSignal(), SELL);
        assertEquals(result[52].getTime(), of(2018, 4, 18, 0, 0));
        assertEquals(result[52].getSignal(), BUY);
        assertEquals(result[65].getTime(), of(2018, 5, 1, 0, 0));
        assertEquals(result[65].getSignal(), SELL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    @Test
    public void testCenterOfGravityAnalyzerWithPeriodTen() {
        DPORequest indicatorRequest = buildIndicatorRequest();
        indicatorRequest.setPeriod(10);
        indicatorRequest.setMovingAverageType(EXPONENTIAL_MOVING_AVERAGE);
        IndicatorResult[] indicatorResults = new DetrendedPriceOscillator(indicatorRequest).getResult();
        DPOAnalyzerResult[] result = new DPOAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getSignal(), BUY);
        assertEquals(result[66].getTime(), of(2018, 5, 2, 0, 0));
        assertEquals(result[66].getSignal(), SELL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    @Override
    protected DPORequest buildIndicatorRequest() {
        return DPORequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .build();
    }

}
