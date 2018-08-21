package pro.crypto.analyzer.cci;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.cci.CCIRequest;
import pro.crypto.indicator.cci.CommodityChannelIndex;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.SecurityLevel;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.SecurityLevel.OVERSOLD;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.NORMAL;
import static pro.crypto.model.Strength.UNDEFINED;
import static pro.crypto.model.Strength.WEAK;

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
        assertNull(result[0].getIndicatorValue());
        assertEquals(result[0].getSecurityLevel(), SecurityLevel.UNDEFINED);
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[0].getStrength(), UNDEFINED);
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-136.0742947841));
        assertEquals(result[19].getSecurityLevel(), OVERSOLD);
        assertEquals(result[19].getSignal(), NEUTRAL);
        assertEquals(result[19].getStrength(), UNDEFINED);
        assertEquals(result[20].getIndicatorValue(), toBigDecimal(-87.475042379));
        assertEquals(result[20].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[20].getSignal(), BUY);
        assertEquals(result[20].getStrength(), NORMAL);
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(99.6051288064));
        assertEquals(result[31].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[31].getSignal(), SELL);
        assertEquals(result[31].getStrength(), NORMAL);
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(156.3372375587));
        assertEquals(result[32].getSecurityLevel(), SecurityLevel.OVERBOUGHT);
        assertEquals(result[32].getSignal(), BUY);
        assertEquals(result[32].getStrength(), NORMAL);
        assertEquals(result[47].getIndicatorValue(), toBigDecimal(110.7520826323));
        assertEquals(result[47].getSecurityLevel(), SecurityLevel.OVERBOUGHT);
        assertEquals(result[47].getSignal(), SELL);
        assertEquals(result[47].getStrength(), WEAK);
        assertEquals(result[52].getIndicatorValue(), toBigDecimal(118.3769691121));
        assertEquals(result[52].getSecurityLevel(), SecurityLevel.OVERBOUGHT);
        assertEquals(result[52].getSignal(), BUY);
        assertEquals(result[52].getStrength(), NORMAL);
        assertEquals(result[64].getIndicatorValue(), toBigDecimal(60.6277911675));
        assertEquals(result[64].getSecurityLevel(), SecurityLevel.NORMAL);
        assertEquals(result[64].getSignal(), SELL);
        assertEquals(result[64].getStrength(), NORMAL);
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-110.2589384038));
        assertEquals(result[72].getSecurityLevel(), SecurityLevel.OVERSOLD);
        assertEquals(result[72].getSignal(), SELL);
        assertEquals(result[72].getStrength(), NORMAL);
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