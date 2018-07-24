package pro.crypto.analyzer.ac;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.ac.ACRequest;
import pro.crypto.indicator.ac.AccelerationDecelerationOscillator;
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

public class ACAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAccelerationDecelerationOscillatorAnalyzer() {
        IndicatorResult[] indicatorResults = new AccelerationDecelerationOscillator(buildIndicatorRequest()).getResult();
        ACAnalyzeResult[] result = new ACAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertNull(result[36].getIndicatorValue());
        assertEquals(result[36].getSignal(), NEUTRAL);
        assertEquals(result[37].getIndicatorValue(), toBigDecimal(17.2272410588));
        assertEquals(result[37].getSignal(), NEUTRAL);
        assertEquals(result[39].getIndicatorValue(), toBigDecimal(15.6172110588));
        assertEquals(result[39].getSignal(), SELL);
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(19.8794633529));
        assertEquals(result[45].getSignal(), BUY);
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(-39.5674152353));
        assertEquals(result[68].getSignal(), SELL);
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-19.5161944118));
        assertEquals(result[72].getSignal(), BUY);
    }

    private IndicatorRequest buildIndicatorRequest() {
        return ACRequest.builder()
                .originalData(originalData)
                .slowPeriod(5)
                .fastPeriod(34)
                .smoothedPeriod(5)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return ACAnalyzeRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}