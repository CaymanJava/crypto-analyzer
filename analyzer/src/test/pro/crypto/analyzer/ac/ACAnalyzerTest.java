package pro.crypto.analyzer.ac;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.ac.ACRequest;
import pro.crypto.indicator.ac.AccelerationDecelerationOscillator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.Signal.*;

public class ACAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testAccelerationDecelerationOscillatorAnalyzer() {
        IndicatorResult[] indicatorResults = new AccelerationDecelerationOscillator(buildIndicatorRequest()).getResult();
        ACAnalyzeResult[] result = new ACAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[36].getTime(), of(2018, 4, 2, 0, 0));
        assertEquals(result[36].getSignal(), NEUTRAL);
        assertEquals(result[37].getTime(), of(2018, 4, 3, 0, 0));
        assertEquals(result[37].getSignal(), NEUTRAL);
        assertEquals(result[39].getTime(), of(2018, 4, 5, 0, 0));
        assertEquals(result[39].getSignal(), SELL);
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getSignal(), BUY);
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getSignal(), SELL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), BUY);
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return ACRequest.builder()
                .originalData(originalData)
                .slowPeriod(5)
                .fastPeriod(34)
                .smoothedPeriod(5)
                .build();
    }

}