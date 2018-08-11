package pro.crypto.analyzer.adx;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.adx.ADXRequest;
import pro.crypto.indicator.adx.AverageDirectionalMovementIndex;
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
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.NEUTRAL;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.Strength.NORMAL;
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
        assertNull(result[25].getIndicatorValue());
        assertEquals(result[25].getSignal(), NEUTRAL);
        assertEquals(result[25].getStrength(), NORMAL);
        assertEquals(result[26].getIndicatorValue(), toBigDecimal(43.1045848871));
        assertEquals(result[26].getSignal(), NEUTRAL);
        assertEquals(result[26].getStrength(), NORMAL);
        assertEquals(result[28].getIndicatorValue(), toBigDecimal(36.1905620414));
        assertEquals(result[28].getSignal(), BUY);
        assertEquals(result[28].getStrength(), NORMAL);
        assertEquals(result[33].getIndicatorValue(), toBigDecimal(21.7463844666));
        assertEquals(result[33].getSignal(), SELL);
        assertEquals(result[33].getStrength(), NORMAL);
        assertEquals(result[35].getIndicatorValue(), toBigDecimal(19.2631215658));
        assertEquals(result[35].getSignal(), NEUTRAL);
        assertEquals(result[35].getStrength(), WEAK);
        assertEquals(result[56].getIndicatorValue(), toBigDecimal(17.7528644891));
        assertEquals(result[56].getSignal(), BUY);
        assertEquals(result[56].getStrength(), WEAK);
        assertEquals(result[65].getIndicatorValue(), toBigDecimal(24.7670170996));
        assertEquals(result[65].getSignal(), SELL);
        assertEquals(result[65].getStrength(), NORMAL);
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