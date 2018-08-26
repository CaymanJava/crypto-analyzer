package pro.crypto.analyzer.cci;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.cci.CCIRequest;
import pro.crypto.indicator.cci.CommodityChannelIndex;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.*;

public class CCIAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testCommodityChannelIndexAnalyzer() {
        IndicatorResult[] indicatorResults = new CommodityChannelIndex(buildIndicatorRequest()).getResult();
        CCIAnalyzerResult[] result = new CCIAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[0].getSecurityLevel(), SecurityLevel.UNDEFINED);
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[19].getSecurityLevel(), SecurityLevel.OVERSOLD);
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertEquals(result[20].getSignalStrength(), new SignalStrength(BUY, NORMAL));
        assertEquals(result[20].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getSignalStrength(), new SignalStrength(SELL, NORMAL));
        assertEquals(result[31].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getSignalStrength(), new SignalStrength(BUY, NORMAL));
        assertEquals(result[32].getSecurityLevel(), SecurityLevel.OVERBOUGHT);
        assertEquals(result[47].getTime(), of(2018, 4, 13, 0, 0));
        assertEquals(result[47].getSignalStrength(), new SignalStrength(SELL, WEAK));
        assertEquals(result[47].getSecurityLevel(), SecurityLevel.OVERBOUGHT);
        assertEquals(result[52].getTime(), of(2018, 4, 18, 0, 0));
        assertEquals(result[52].getSignalStrength(), new SignalStrength(BUY, NORMAL));
        assertEquals(result[52].getSecurityLevel(), SecurityLevel.OVERBOUGHT);
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getSignalStrength(), new SignalStrength(SELL, NORMAL));
        assertEquals(result[64].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignalStrength(), new SignalStrength(SELL, NORMAL));
        assertEquals(result[72].getSecurityLevel(), SecurityLevel.OVERSOLD);
    }

    private IndicatorRequest buildIndicatorRequest() {
        return CCIRequest.builder()
                .originalData(originalData)
                .period(20)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}