package pro.crypto.analyzer.cfo;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.cfo.CFORequest;
import pro.crypto.indicator.cfo.ChandeForecastOscillator;
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
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class CFOAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testChandeForecastOscillatorAnalyzer() {
        IndicatorResult[] indicatorResults = new ChandeForecastOscillator(buildIndicatorRequest()).getResult();
        CFOAnalyzerResult[] result = new CFOAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[4].getIndicatorValue(), toBigDecimal(3.7209067914));
        assertEquals(result[4].getSignal(), NEUTRAL);
        assertEquals(result[13].getIndicatorValue(), toBigDecimal(-5.7267646119));
        assertEquals(result[13].getSignal(), SELL);
        assertEquals(result[16].getIndicatorValue(), toBigDecimal(0.7143951108));
        assertEquals(result[16].getSignal(), BUY);
        assertEquals(result[40].getIndicatorValue(), toBigDecimal(-1.4949282216));
        assertEquals(result[40].getSignal(), SELL);
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(5.1221371353));
        assertEquals(result[43].getSignal(), BUY);
        assertEquals(result[64].getIndicatorValue(), toBigDecimal(-1.2582783514));
        assertEquals(result[64].getSignal(), SELL);
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-1.5280234256));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    private IndicatorRequest buildIndicatorRequest() {
        return CFORequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .period(5)
                .movingAveragePeriod(10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}