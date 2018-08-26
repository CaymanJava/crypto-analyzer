package pro.crypto.analyzer.adx;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.adx.ADXRequest;
import pro.crypto.indicator.adx.AverageDirectionalMovementIndex;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.NORMAL;
import static pro.crypto.model.Strength.UNDEFINED;
import static pro.crypto.model.Strength.WEAK;

public class ADXAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAverageDirectionalMovementIndexAnalyzer() {
        IndicatorResult[] indicatorResults = new AverageDirectionalMovementIndex(buildIndicatorRequest()).getResult();
        ADXAnalyzerResult[] result = new ADXAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[25].getTime(), of(2018, 3, 22, 0, 0));
        assertNull(result[25].getEntryPoint());
        assertEquals(result[25].getSignal(), NEUTRAL);
        assertEquals(result[25].getTrendStrength(), new TrendStrength(Trend.UNDEFINED, UNDEFINED));
        assertEquals(result[26].getTime(), of(2018, 3, 23, 0, 0));
        assertNull(result[26].getEntryPoint());
        assertEquals(result[26].getSignal(), NEUTRAL);
        assertEquals(result[26].getTrendStrength(), new TrendStrength(Trend.UNDEFINED, NORMAL));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getEntryPoint(), toBigDecimal(1220.12));
        assertEquals(result[28].getSignal(), BUY);
        assertEquals(result[28].getTrendStrength(), new TrendStrength(Trend.UNDEFINED, NORMAL));
        assertEquals(result[33].getTime(), of(2018, 3, 30, 0, 0));
        assertEquals(result[33].getEntryPoint(), toBigDecimal(1253.4399));
        assertEquals(result[33].getSignal(), SELL);
        assertEquals(result[33].getTrendStrength(), new TrendStrength(Trend.UNDEFINED, NORMAL));
        assertEquals(result[35].getTime(), of(2018, 4, 1, 0, 0));
        assertNull(result[35].getEntryPoint());
        assertEquals(result[35].getSignal(), NEUTRAL);
        assertEquals(result[35].getTrendStrength(), new TrendStrength(Trend.UNDEFINED, WEAK));
        assertEquals(result[56].getTime(), of(2018, 4, 22, 0, 0));
        assertEquals(result[56].getEntryPoint(), toBigDecimal(1419.64));
        assertEquals(result[56].getSignal(), BUY);
        assertEquals(result[56].getTrendStrength(), new TrendStrength(Trend.UNDEFINED, WEAK));
        assertEquals(result[65].getTime(), of(2018, 5, 1, 0, 0));
        assertEquals(result[65].getEntryPoint(), toBigDecimal(1412.92));
        assertEquals(result[65].getSignal(), SELL);
        assertEquals(result[65].getTrendStrength(), new TrendStrength(Trend.UNDEFINED, NORMAL));
    }

    private IndicatorRequest buildIndicatorRequest() {
        return ADXRequest.builder()
                .originalData(originalData)
                .period(14)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}