package pro.crypto.analyzer.cc;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.cc.CCRequest;
import pro.crypto.indicator.cc.CoppockCurve;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class CCAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testCoppockCurveAnalyzer() {
        IndicatorResult[] indicatorResults = new CoppockCurve(buildIndicatorRequest()).getResult();
        CCAnalyzerResult[] result = new CCAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[23].getIndicatorValue(), toBigDecimal(-15.220206831));
        assertEquals(result[23].getSignal(), NEUTRAL);
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(-1.6908115926));
        assertEquals(result[31].getSignal(), NEUTRAL);
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(1.9610278079));
        assertEquals(result[32].getSignal(), BUY);
        assertEquals(result[33].getIndicatorValue(), toBigDecimal(6.3629844112));
        assertEquals(result[33].getSignal(), NEUTRAL);
        assertEquals(result[70].getIndicatorValue(), toBigDecimal(0.4911890589));
        assertEquals(result[70].getSignal(), NEUTRAL);
        assertEquals(result[71].getIndicatorValue(), toBigDecimal(-1.9091074699));
        assertEquals(result[71].getSignal(), SELL);
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-5.2733431323));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    private IndicatorRequest buildIndicatorRequest() {
        return CCRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .shortROCPeriod(11)
                .longROCPeriod(14)
                .period(10)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}