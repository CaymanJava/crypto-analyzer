package pro.crypto.analyzer.bb;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.bb.BBRequest;
import pro.crypto.indicator.bb.BollingerBands;
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
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class BBAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testBollingerBandsAnalyzer() {
        IndicatorResult[] indicatorResults = new BollingerBands(buildIndicatorRequest()).getResult();
        BBAnalyzerResult[] result = new BBAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getCrossUpperBand());
        assertNull(result[0].getCrossLowerBand());
        assertNull(result[0].getCrossMiddleBand());
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(1251.06901));
        assertEquals(result[19].getCrossUpperBand(), false);
        assertEquals(result[19].getCrossLowerBand(), true);
        assertEquals(result[19].getCrossMiddleBand(), false);
        assertEquals(result[28].getIndicatorValue(), toBigDecimal(1192.538015));
        assertEquals(result[28].getCrossUpperBand(), false);
        assertEquals(result[28].getCrossLowerBand(), false);
        assertEquals(result[28].getCrossMiddleBand(), true);
        assertEquals(result[30].getIndicatorValue(), toBigDecimal(1190.895515));
        assertEquals(result[30].getCrossUpperBand(), true);
        assertEquals(result[30].getCrossLowerBand(), false);
        assertEquals(result[30].getCrossMiddleBand(), false);
        assertEquals(result[49].getIndicatorValue(), toBigDecimal(1326.647));
        assertEquals(result[49].getCrossUpperBand(), false);
        assertEquals(result[49].getCrossLowerBand(), false);
        assertEquals(result[49].getCrossMiddleBand(), true);
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(1371.25651));
        assertEquals(result[57].getCrossUpperBand(), true);
        assertEquals(result[57].getCrossLowerBand(), false);
        assertEquals(result[57].getCrossMiddleBand(), false);
        assertEquals(result[63].getIndicatorValue(), toBigDecimal(1415.354));
        assertEquals(result[63].getCrossUpperBand(), true);
        assertEquals(result[63].getCrossLowerBand(), false);
        assertEquals(result[63].getCrossMiddleBand(), false);
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1427.10249));
        assertEquals(result[72].getCrossUpperBand(), false);
        assertEquals(result[72].getCrossLowerBand(), false);
        assertEquals(result[72].getCrossMiddleBand(), false);
    }

    private IndicatorRequest buildIndicatorRequest() {
        return BBRequest.builder()
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