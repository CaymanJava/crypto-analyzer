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
        assertNull(result[0].getCrossUpperBand());
        assertNull(result[0].getCrossLowerBand());
        assertNull(result[0].getCrossMiddleBand());
        assertEquals(result[4].getIndicatorValue(), toBigDecimal(1304.6));
        assertEquals(result[4].getCrossUpperBand(), false);
        assertEquals(result[4].getCrossLowerBand(), false);
        assertEquals(result[4].getCrossMiddleBand(), true);
        assertEquals(result[10].getIndicatorValue(), toBigDecimal(1259.9399));
        assertEquals(result[10].getCrossUpperBand(), true);
        assertEquals(result[10].getCrossLowerBand(), false);
        assertEquals(result[10].getCrossMiddleBand(), true);
        assertEquals(result[16].getIndicatorValue(), toBigDecimal(1222.29));
        assertEquals(result[16].getCrossUpperBand(), false);
        assertEquals(result[16].getCrossLowerBand(), true);
        assertEquals(result[16].getCrossMiddleBand(), true);
        assertEquals(result[18].getIndicatorValue(), toBigDecimal(1199.16));
        assertEquals(result[18].getCrossUpperBand(), true);
        assertEquals(result[18].getCrossLowerBand(), false);
        assertEquals(result[18].getCrossMiddleBand(), true);
        assertEquals(result[27].getIndicatorValue(), toBigDecimal(1163.37));
        assertEquals(result[27].getCrossUpperBand(), false);
        assertEquals(result[27].getCrossLowerBand(), true);
        assertEquals(result[27].getCrossMiddleBand(), true);
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1360.7));
        assertEquals(result[43].getCrossUpperBand(), false);
        assertEquals(result[43].getCrossLowerBand(), true);
        assertEquals(result[43].getCrossMiddleBand(), true);
        assertEquals(result[63].getIndicatorValue(), toBigDecimal(1484.78));
        assertEquals(result[63].getCrossUpperBand(), true);
        assertEquals(result[63].getCrossLowerBand(), false);
        assertEquals(result[63].getCrossMiddleBand(), true);
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1362.61));
        assertEquals(result[72].getCrossUpperBand(), false);
        assertEquals(result[72].getCrossLowerBand(), false);
        assertEquals(result[72].getCrossMiddleBand(), true);
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