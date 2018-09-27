package pro.crypto.helper.divergence;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.adl.ADLRequest;
import pro.crypto.indicator.adl.ADLResult;
import pro.crypto.indicator.adl.AccumulationDistributionLine;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static pro.crypto.helper.divergence.DivergenceClass.CLASSIC;
import static pro.crypto.helper.divergence.DivergenceClass.EXTENDED;
import static pro.crypto.helper.divergence.DivergenceClass.HIDDEN;
import static pro.crypto.helper.divergence.DivergenceType.BEARISH;
import static pro.crypto.helper.divergence.DivergenceType.BULLISH;

public class DivergenceTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testDivergenceFinder() {
        ADLResult[] result = new AccumulationDistributionLine(buildIndicatorRequest()).getResult();
        DivergenceResult[] divergenceResult = new Divergence(buildDivergenceRequest(IndicatorResultExtractor.extract(result))).find();
        assertTrue(divergenceResult.length == 11);
        assertEquals(divergenceResult[0].getDivergenceType(), BULLISH);
        assertEquals(divergenceResult[0].getDivergenceClass(), HIDDEN);
        assertEquals(divergenceResult[0].getIndexFrom(), 19);
        assertEquals(divergenceResult[0].getIndexTo(), 21);
        assertEquals(divergenceResult[1].getDivergenceType(), BEARISH);
        assertEquals(divergenceResult[1].getDivergenceClass(), CLASSIC);
        assertEquals(divergenceResult[1].getIndexFrom(), 20);
        assertEquals(divergenceResult[1].getIndexTo(), 29);
        assertEquals(divergenceResult[2].getDivergenceType(), BEARISH);
        assertEquals(divergenceResult[2].getDivergenceClass(), CLASSIC);
        assertEquals(divergenceResult[2].getIndexFrom(), 20);
        assertEquals(divergenceResult[2].getIndexTo(), 30);
        assertEquals(divergenceResult[3].getDivergenceType(), BEARISH);
        assertEquals(divergenceResult[3].getDivergenceClass(), CLASSIC);
        assertEquals(divergenceResult[3].getIndexFrom(), 30);
        assertEquals(divergenceResult[3].getIndexTo(), 33);
        assertEquals(divergenceResult[4].getDivergenceType(), BEARISH);
        assertEquals(divergenceResult[4].getDivergenceClass(), CLASSIC);
        assertEquals(divergenceResult[4].getIndexFrom(), 8);
        assertEquals(divergenceResult[4].getIndexTo(), 34);
        assertEquals(divergenceResult[5].getDivergenceType(), BEARISH);
        assertEquals(divergenceResult[5].getDivergenceClass(), CLASSIC);
        assertEquals(divergenceResult[5].getIndexFrom(), 8);
        assertEquals(divergenceResult[5].getIndexTo(), 36);
        assertEquals(divergenceResult[6].getDivergenceType(), BULLISH);
        assertEquals(divergenceResult[6].getDivergenceClass(), EXTENDED);
        assertEquals(divergenceResult[6].getIndexFrom(), 35);
        assertEquals(divergenceResult[6].getIndexTo(), 37);
        assertEquals(divergenceResult[7].getDivergenceType(), BEARISH);
        assertEquals(divergenceResult[7].getDivergenceClass(), CLASSIC);
        assertEquals(divergenceResult[7].getIndexFrom(), 36);
        assertEquals(divergenceResult[7].getIndexTo(), 38);
        assertEquals(divergenceResult[8].getDivergenceType(), BULLISH);
        assertEquals(divergenceResult[8].getDivergenceClass(), HIDDEN);
        assertEquals(divergenceResult[8].getIndexFrom(), 37);
        assertEquals(divergenceResult[8].getIndexTo(), 39);
        assertEquals(divergenceResult[9].getDivergenceType(), BEARISH);
        assertEquals(divergenceResult[9].getDivergenceClass(), EXTENDED);
        assertEquals(divergenceResult[9].getIndexFrom(), 46);
        assertEquals(divergenceResult[9].getIndexTo(), 56);
        assertEquals(divergenceResult[10].getDivergenceType(), BULLISH);
        assertEquals(divergenceResult[10].getDivergenceClass(), EXTENDED);
        assertEquals(divergenceResult[10].getIndexFrom(), 60);
        assertEquals(divergenceResult[10].getIndexTo(), 64);
    }

    private IndicatorRequest buildIndicatorRequest() {
        return ADLRequest.builder()
                .originalData(originalData)
                .build();
    }

    private DivergenceRequest buildDivergenceRequest(BigDecimal[] indicatorValues) {
        return DivergenceRequest.builder()
                .originalData(originalData)
                .indicatorValues(indicatorValues)
                .build();
    }

}