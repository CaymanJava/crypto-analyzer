package pro.crypto.analyzer.atrb;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.atrb.ATRBRequest;
import pro.crypto.indicator.atrb.AverageTrueRangeBands;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ATRBAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAverageTrueRangeBandsAnalyzer() {
        IndicatorResult[] indicatorResults = new AverageTrueRangeBands(buildIndicatorRequest()).getResult();
        ATRBAnalyzerResult[] result = new ATRBAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[4].getIndicatorValue(), toBigDecimal(1304.6));
        assertEquals(result[4].getSignal(), NEUTRAL);
        assertEquals(result[10].getIndicatorValue(), toBigDecimal(1259.9399));
        assertEquals(result[10].getSignal(), SELL);
        assertEquals(result[16].getIndicatorValue(), toBigDecimal(1222.29));
        assertEquals(result[16].getSignal(), BUY);
        assertEquals(result[18].getIndicatorValue(), toBigDecimal(1199.16));
        assertEquals(result[18].getSignal(), SELL);
        assertEquals(result[27].getIndicatorValue(), toBigDecimal(1163.37));
        assertEquals(result[27].getSignal(), BUY);
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1360.7));
        assertEquals(result[43].getSignal(), BUY);
        assertEquals(result[63].getIndicatorValue(), toBigDecimal(1484.78));
        assertEquals(result[63].getSignal(), SELL);
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1362.61));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    private IndicatorRequest buildIndicatorRequest() {
        return ATRBRequest.builder()
                .originalData(originalData)
                .period(5)
                .shift(1)
                .priceType(CLOSE)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}