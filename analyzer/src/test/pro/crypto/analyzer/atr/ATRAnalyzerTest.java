package pro.crypto.analyzer.atr;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.atr.ATRRequest;
import pro.crypto.indicator.atr.AverageTrueRange;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;

public class ATRAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAverageTrueRangeAnalyzer() {
        IndicatorResult[] indicatorResults = new AverageTrueRange(buildIndicatorRequest()).getResult();
        ATRAnalyzerResult[] result = new ATRAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertFalse(result[0].getStartTrend());
        assertFalse(result[0].getTrendExist());
        assertEquals(result[9].getIndicatorValue(), toBigDecimal(31.81202));
        assertFalse(result[9].getStartTrend());
        assertFalse(result[9].getTrendExist());
        assertEquals(result[18].getIndicatorValue(), toBigDecimal(32.6122649243));
        assertFalse(result[18].getStartTrend());
        assertTrue(result[18].getTrendExist());
        assertEquals(result[27].getIndicatorValue(), toBigDecimal(36.2405423251));
        assertTrue(result[27].getStartTrend());
        assertTrue(result[27].getTrendExist());
        assertEquals(result[37].getIndicatorValue(), toBigDecimal(38.1619396657));
        assertFalse(result[37].getStartTrend());
        assertFalse(result[37].getTrendExist());
        assertEquals(result[44].getIndicatorValue(), toBigDecimal(39.4189378381));
        assertTrue(result[44].getStartTrend());
        assertTrue(result[44].getTrendExist());
        assertEquals(result[61].getIndicatorValue(), toBigDecimal(37.1283885232));
        assertTrue(result[61].getStartTrend());
        assertTrue(result[61].getTrendExist());
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(33.2115532806));
        assertFalse(result[72].getStartTrend());
        assertFalse(result[72].getTrendExist());
    }

    private IndicatorRequest buildIndicatorRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(10)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}