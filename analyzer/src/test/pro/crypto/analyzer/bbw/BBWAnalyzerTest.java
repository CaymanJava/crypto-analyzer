package pro.crypto.analyzer.bbw;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.bbw.BBWRequest;
import pro.crypto.indicator.bbw.BollingerBandsWidth;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class BBWAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testBollingerBandsWidthAnalyzer() {
        IndicatorResult[] indicatorResults = new BollingerBandsWidth(buildIndicatorRequest()).getResult();
        BBWAnalyzerResult[] result = new BBWAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertFalse(result[0].getStartTrend());
        assertFalse(result[0].getTrendExist());
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(0.1440312856));
        assertFalse(result[19].getStartTrend());
        assertTrue(result[19].getTrendExist());
        assertEquals(result[50].getIndicatorValue(), toBigDecimal(0.1352896482));
        assertFalse(result[50].getStartTrend());
        assertTrue(result[50].getTrendExist());
        assertEquals(result[51].getIndicatorValue(), toBigDecimal(0.118018491));
        assertFalse(result[51].getStartTrend());
        assertFalse(result[51].getTrendExist());
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(0.1238978499));
        assertFalse(result[57].getStartTrend());
        assertFalse(result[57].getTrendExist());
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(0.1346238366));
        assertTrue(result[58].getStartTrend());
        assertTrue(result[58].getTrendExist());
        assertEquals(result[59].getIndicatorValue(), toBigDecimal(0.142133706));
        assertFalse(result[59].getStartTrend());
        assertTrue(result[59].getTrendExist());
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(0.1358005246));
        assertFalse(result[68].getStartTrend());
        assertTrue(result[68].getTrendExist());
        assertEquals(result[69].getIndicatorValue(), toBigDecimal(0.1203640324));
        assertFalse(result[69].getStartTrend());
        assertFalse(result[69].getTrendExist());
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(0.112317199));
        assertFalse(result[72].getStartTrend());
        assertFalse(result[72].getTrendExist());
    }

    private IndicatorRequest buildIndicatorRequest() {
        return BBWRequest.builder()
                .originalData(originalData)
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}