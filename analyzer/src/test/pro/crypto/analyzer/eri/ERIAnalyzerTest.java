package pro.crypto.analyzer.eri;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.eri.ERIRequest;
import pro.crypto.indicator.eri.ElderRayIndex;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.SignalStrength;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.*;

public class ERIAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testEhlersFisherTransformAnalyzer() {
        IndicatorResult[] indicatorResults = new ElderRayIndex(buildIndicatorRequest()).getResult();
        ERIAnalyzerResult[] result = new ERIAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[37].getTime(), of(2018, 4, 3, 0, 0));
        assertEquals(result[37].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[40].getTime(), of(2018, 4, 6, 0, 0));
        assertEquals(result[40].getSignalStrength(), new SignalStrength(SELL, NORMAL));
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[54].getTime(), of(2018, 4, 20, 0, 0));
        assertEquals(result[54].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getSignalStrength(), new SignalStrength(SELL, NORMAL));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignalStrength(), new SignalStrength(SELL, STRONG));
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return ERIRequest.builder()
                .originalData(originalData)
                .period(13)
                .signalLinePeriod(13)
                .smoothLinePeriod(2)
                .build();
    }

}
