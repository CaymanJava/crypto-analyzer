package pro.crypto.analyzer.ce;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.ce.CERequest;
import pro.crypto.indicator.ce.ChandelierExit;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.Signal.*;

public class CEAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testChandelierExitAnalyzer() {
        IndicatorResult[] indicatorResults = new ChandelierExit(buildIndicatorRequest()).getResult();
        CEAnalyzerResult[] result = new CEAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[21].getTime(), of(2018, 3, 18, 0, 0));
        assertEquals(result[21].getSignal(), NEUTRAL);
        assertEquals(result[29].getTime(), of(2018, 3, 26, 0, 0));
        assertEquals(result[29].getSignal(), BUY);
        assertEquals(result[56].getTime(), of(2018, 4, 22, 0, 0));
        assertEquals(result[56].getSignal(), BUY);
        assertEquals(result[66].getTime(), of(2018, 5, 2, 0, 0));
        assertEquals(result[66].getSignal(), SELL);
        assertEquals(result[67].getTime(), of(2018, 5, 3, 0, 0));
        assertEquals(result[67].getSignal(), NEUTRAL);
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getSignal(), SELL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return CERequest.builder()
                .originalData(originalData)
                .period(22)
                .longFactor(3)
                .shortFactor(3)
                .build();
    }

}
