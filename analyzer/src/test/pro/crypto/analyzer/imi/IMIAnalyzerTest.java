package pro.crypto.analyzer.imi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.imi.IMIRequest;
import pro.crypto.indicator.imi.IntradayMomentumIndex;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.SecurityLevel;
import pro.crypto.model.SignalStrength;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.SecurityLevel.OVERBOUGHT;
import static pro.crypto.model.SecurityLevel.OVERSOLD;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.*;

public class IMIAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testIntradayMomentumIndexIndexAnalyzer() {
        IndicatorResult[] indicatorResults = new IntradayMomentumIndex(buildIndicatorRequest()).getResult();
        IMIAnalyzerResult[] result = new IMIAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[0].getSecurityLevel(), SecurityLevel.UNDEFINED);
        assertEquals(result[24].getTime(), of(2018, 3, 21, 0, 0));
        assertEquals(result[24].getSignalStrength(), new SignalStrength(SELL, NORMAL));
        assertEquals(result[24].getSecurityLevel(), OVERSOLD);
        assertEquals(result[27].getTime(), of(2018, 3, 24, 0, 0));
        assertEquals(result[27].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[27].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[38].getTime(), of(2018, 4, 4, 0, 0));
        assertEquals(result[38].getSignalStrength(), new SignalStrength(BUY, WEAK));
        assertEquals(result[38].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getSignalStrength(), new SignalStrength(BUY, NORMAL));
        assertEquals(result[43].getSecurityLevel(), OVERBOUGHT);
        assertEquals(result[48].getTime(), of(2018, 4, 14, 0, 0));
        assertEquals(result[48].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[48].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[56].getTime(), of(2018, 4, 22, 0, 0));
        assertEquals(result[56].getSignalStrength(), new SignalStrength(BUY, WEAK));
        assertEquals(result[56].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[60].getTime(), of(2018, 4, 26, 0, 0));
        assertEquals(result[60].getSignalStrength(), new SignalStrength(SELL, WEAK));
        assertEquals(result[60].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[72].getSecurityLevel(), SecurityLevel.NORMAL);
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return IMIRequest.builder()
                .originalData(originalData)
                .period(20)
                .build();
    }

}
