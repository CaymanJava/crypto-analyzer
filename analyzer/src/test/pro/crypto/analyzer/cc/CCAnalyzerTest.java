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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[23].getTime(), of(2018, 3, 20, 0, 0));
        assertEquals(result[23].getSignal(), NEUTRAL);
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getSignal(), NEUTRAL);
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getSignal(), BUY);
        assertEquals(result[33].getTime(), of(2018, 3, 30, 0, 0));
        assertEquals(result[33].getSignal(), NEUTRAL);
        assertEquals(result[70].getTime(), of(2018, 5, 6, 0, 0));
        assertEquals(result[70].getSignal(), NEUTRAL);
        assertEquals(result[71].getTime(), of(2018, 5, 7, 0, 0));
        assertEquals(result[71].getSignal(), SELL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
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