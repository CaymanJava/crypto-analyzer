package pro.crypto.analyzer.ao;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.ao.AORequest;
import pro.crypto.indicator.ao.AwesomeOscillator;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.Signal.*;

public class AOAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAccelerationDecelerationOscillatorAnalyzer() {
        IndicatorResult[] indicatorResults = new AwesomeOscillator(buildIndicatorRequest()).getResult();
        AOAnalyzerResult[] result = new AOAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[53].getTime(), of(2018, 4, 19, 0, 0));
        assertEquals(result[53].getSignal(), NEUTRAL);
        assertEquals(result[54].getTime(), of(2018, 4, 20, 0, 0));
        assertEquals(result[54].getSignal(), BUY);
        assertEquals(result[62].getTime(), of(2018, 4, 28, 0, 0));
        assertEquals(result[62].getSignal(), SELL);
        assertEquals(result[71].getTime(), of(2018, 5, 7, 0, 0));
        assertEquals(result[71].getSignal(), SELL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    private IndicatorRequest buildIndicatorRequest() {
        return AORequest.builder()
                .originalData(originalData)
                .slowPeriod(5)
                .fastPeriod(34)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}