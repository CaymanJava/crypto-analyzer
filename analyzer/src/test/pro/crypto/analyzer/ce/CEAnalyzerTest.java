package pro.crypto.analyzer.ce;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.ce.CERequest;
import pro.crypto.indicator.ce.ChandelierExit;
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

public class CEAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testChandelierExitAnalyzer() {
        IndicatorResult[] indicatorResults = new ChandelierExit(buildIndicatorRequest()).getResult();
        CEAnalyzerResult[] result = new CEAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[21].getIndicatorValue(), toBigDecimal(1260.0804090908));
        assertEquals(result[21].getSignal(), NEUTRAL);
        assertEquals(result[29].getIndicatorValue(), toBigDecimal(1213.4778827282));
        assertEquals(result[29].getSignal(), BUY);
        assertEquals(result[56].getIndicatorValue(), toBigDecimal(1388.9657641606));
        assertEquals(result[56].getSignal(), BUY);
        assertEquals(result[66].getIndicatorValue(), toBigDecimal(1413.9083180680));
        assertEquals(result[66].getSignal(), SELL);
        assertEquals(result[67].getIndicatorValue(), toBigDecimal(1427.0714100260));
        assertEquals(result[67].getSignal(), NEUTRAL);
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(1412.3025358843));
        assertEquals(result[68].getSignal(), SELL);
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1438.477555866));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    private IndicatorRequest buildIndicatorRequest() {
        return CERequest.builder()
                .originalData(originalData)
                .period(22)
                .longFactor(3)
                .shortFactor(3)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}