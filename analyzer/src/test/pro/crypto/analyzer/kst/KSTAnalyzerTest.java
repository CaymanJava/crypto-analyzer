package pro.crypto.analyzer.kst;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.kst.KSTRequest;
import pro.crypto.indicator.kst.KnowSureThing;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.SignalStrength;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class KSTAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testKnowSureThingAnalyzer() {
        IndicatorResult[] indicatorResults = new KnowSureThing(buildIndicatorRequest()).getResult();
        KSTAnalyzerResult[] result = new KSTAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[55].getTime(), of(2018, 4, 21, 0, 0));
        assertEquals(result[55].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[66].getTime(), of(2018, 5, 2, 0, 0));
        assertEquals(result[66].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[69].getTime(), of(2018, 5, 5, 0, 0));
        assertEquals(result[69].getSignalStrength(), new SignalStrength(BUY, WEAK));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return KSTRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .lightestROCPeriod(10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(9)
                .build();
    }

}
