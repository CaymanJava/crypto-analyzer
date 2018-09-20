package pro.crypto.analyzer.eft;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.eft.EFTRequest;
import pro.crypto.indicator.eft.EhlersFisherTransform;
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

public class EFTAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testEhlersFisherTransformAnalyzer() {
        IndicatorResult[] indicatorResults = new EhlersFisherTransform(buildIndicatorRequest()).getResult();
        EFTAnalyzerResult[] result = new EFTAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[16].getTime(), of(2018, 3, 13, 0, 0));
        assertEquals(result[16].getSignalStrength(), new SignalStrength(BUY, NORMAL));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getSignalStrength(), new SignalStrength(SELL, NORMAL));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getSignalStrength(), new SignalStrength(BUY, NORMAL));
        assertEquals(result[30].getTime(), of(2018, 3, 27, 0, 0));
        assertEquals(result[30].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[39].getTime(), of(2018, 4, 5, 0, 0));
        assertEquals(result[39].getSignalStrength(), new SignalStrength(SELL, NORMAL));
        assertEquals(result[67].getTime(), of(2018, 5, 3, 0, 0));
        assertEquals(result[67].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
    }

    private IndicatorRequest buildIndicatorRequest() {
        return EFTRequest.builder()
                .originalData(originalData)
                .period(10)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}
