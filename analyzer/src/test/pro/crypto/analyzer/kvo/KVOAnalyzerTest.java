package pro.crypto.analyzer.kvo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.kvo.KVORequest;
import pro.crypto.indicator.kvo.KlingerVolumeOscillator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.Signal.*;

public class KVOAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testKlingerVolumeOscillatorAnalyzer() {
        IndicatorResult[] indicatorResults = new KlingerVolumeOscillator(buildIndicatorRequest()).getResult();
        KVOAnalyzerResult[] result = new KVOAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getSignal(), SELL);
        assertEquals(result[54].getTime(), of(2018, 4, 20, 0, 0));
        assertEquals(result[54].getSignal(), BUY);
        assertEquals(result[55].getTime(), of(2018, 4, 21, 0, 0));
        assertEquals(result[55].getSignal(), SELL);
        assertEquals(result[56].getTime(), of(2018, 4, 22, 0, 0));
        assertEquals(result[56].getSignal(), BUY);
        assertEquals(result[60].getTime(), of(2018, 4, 26, 0, 0));
        assertEquals(result[60].getSignal(), SELL);
        assertEquals(result[61].getTime(), of(2018, 4, 27, 0, 0));
        assertEquals(result[61].getSignal(), BUY);
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getSignal(), SELL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return KVORequest.builder()
                .originalData(originalData)
                .shortPeriod(14)
                .longPeriod(25)
                .signalPeriod(13)
                .build();
    }

}
