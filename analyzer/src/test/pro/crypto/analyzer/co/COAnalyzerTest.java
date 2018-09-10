package pro.crypto.analyzer.co;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.co.CORequest;
import pro.crypto.indicator.co.ChaikinOscillator;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.*;

public class COAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testChaikinOscillatorAnalyzer() {
        IndicatorResult[] indicatorResults = new ChaikinOscillator(buildIndicatorRequest()).getResult();
        COAnalyzerResult[] result = new COAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[27].getTime(), of(2018, 3, 24, 0, 0));
        assertEquals(result[27].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getSignalStrength(), new SignalStrength(SELL, WEAK));
        assertEquals(result[34].getTime(), of(2018, 3, 31, 0, 0));
        assertEquals(result[34].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[39].getTime(), of(2018, 4, 5, 0, 0));
        assertEquals(result[39].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getSignalStrength(), new SignalStrength(BUY, NORMAL));
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[53].getTime(), of(2018, 4, 19, 0, 0));
        assertEquals(result[53].getSignalStrength(), new SignalStrength(BUY, NORMAL));
        assertEquals(result[69].getTime(), of(2018, 5, 5, 0, 0));
        assertEquals(result[69].getSignalStrength(), new SignalStrength(BUY, WEAK));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
    }

    private IndicatorRequest buildIndicatorRequest() {
        return CORequest.builder()
                .originalData(originalData)
                .slowPeriod(6)
                .fastPeriod(20)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}