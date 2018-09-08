package pro.crypto.analyzer.cmo;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.cmo.CMORequest;
import pro.crypto.indicator.cmo.ChandeMomentumOscillator;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.*;

public class CMOAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testChandeMomentumOscillatorAnalyzer() {
        IndicatorResult[] indicatorResults = new ChandeMomentumOscillator(buildIndicatorRequest()).getResult();
        CMOAnalyzerResult[] result = new CMOAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[0].getSecurityLevel(), SecurityLevel.UNDEFINED);
        assertEquals(result[8].getTime(), of(2018, 3, 5, 0, 0));
        assertEquals(result[8].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[8].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[10].getTime(), of(2018, 3, 7, 0, 0));
        assertEquals(result[10].getSignalStrength(), new SignalStrength(SELL, NORMAL));
        assertEquals(result[10].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[16].getTime(), of(2018, 3, 13, 0, 0));
        assertEquals(result[16].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[16].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertEquals(result[20].getSignalStrength(), new SignalStrength(BUY, WEAK));
        assertEquals(result[20].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[27].getTime(), of(2018, 3, 24, 0, 0));
        assertEquals(result[27].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[27].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[37].getTime(), of(2018, 4, 3, 0, 0));
        assertEquals(result[37].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[37].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[47].getTime(), of(2018, 4, 13, 0, 0));
        assertEquals(result[47].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[47].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[50].getTime(), of(2018, 4, 16, 0, 0));
        assertEquals(result[50].getSignalStrength(), new SignalStrength(BUY, WEAK));
        assertEquals(result[50].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getSignalStrength(), new SignalStrength(SELL, WEAK));
        assertEquals(result[64].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[72].getSecurityLevel(), SecurityLevel.OVERSOLD);
    }

    private IndicatorRequest buildIndicatorRequest() {
        return CMORequest.builder()
                .originalData(originalData)
                .period(9)
                .signalLinePeriod(10)
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