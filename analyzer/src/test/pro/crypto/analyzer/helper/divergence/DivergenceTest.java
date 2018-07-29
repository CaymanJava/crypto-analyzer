package pro.crypto.analyzer.helper.divergence;

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
import static pro.crypto.analyzer.helper.divergence.DivergenceClass.A_CLASS;
import static pro.crypto.analyzer.helper.divergence.DivergenceClass.B_CLASS;
import static pro.crypto.analyzer.helper.divergence.DivergenceClass.C_CLASS;
import static pro.crypto.analyzer.helper.divergence.DivergenceType.BEARER;
import static pro.crypto.analyzer.helper.divergence.DivergenceType.BULLISH;

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
        assertTrue(divergenceResult.length == 9);
        assertEquals(divergenceResult[0].getDivergenceType(), BULLISH);
        assertEquals(divergenceResult[0].getDivergenceClass(), C_CLASS);
        assertEquals(divergenceResult[0].getIndexFrom(), 7);
        assertEquals(divergenceResult[0].getIndexTo(), 9);
        assertEquals(divergenceResult[1].getDivergenceType(), BULLISH);
        assertEquals(divergenceResult[1].getDivergenceClass(), C_CLASS);
        assertEquals(divergenceResult[1].getIndexFrom(), 7);
        assertEquals(divergenceResult[1].getIndexTo(), 10);
        assertEquals(divergenceResult[2].getDivergenceType(), BULLISH);
        assertEquals(divergenceResult[2].getDivergenceClass(), C_CLASS);
        assertEquals(divergenceResult[2].getIndexFrom(), 7);
        assertEquals(divergenceResult[2].getIndexTo(), 11);
        assertEquals(divergenceResult[3].getDivergenceType(), BULLISH);
        assertEquals(divergenceResult[3].getDivergenceClass(), C_CLASS);
        assertEquals(divergenceResult[3].getIndexFrom(), 10);
        assertEquals(divergenceResult[3].getIndexTo(), 11);
        assertEquals(divergenceResult[4].getDivergenceType(), BULLISH);
        assertEquals(divergenceResult[4].getDivergenceClass(), C_CLASS);
        assertEquals(divergenceResult[4].getIndexFrom(), 19);
        assertEquals(divergenceResult[4].getIndexTo(), 21);
        assertEquals(divergenceResult[5].getDivergenceType(), BEARER);
        assertEquals(divergenceResult[5].getDivergenceClass(), B_CLASS);
        assertEquals(divergenceResult[5].getIndexFrom(), 20);
        assertEquals(divergenceResult[5].getIndexTo(), 29);
        assertEquals(divergenceResult[6].getDivergenceType(), BEARER);
        assertEquals(divergenceResult[6].getDivergenceClass(), A_CLASS);
        assertEquals(divergenceResult[6].getIndexFrom(), 20);
        assertEquals(divergenceResult[6].getIndexTo(), 30);
        assertEquals(divergenceResult[7].getDivergenceType(), BEARER);
        assertEquals(divergenceResult[7].getDivergenceClass(), B_CLASS);
        assertEquals(divergenceResult[7].getIndexFrom(), 30);
        assertEquals(divergenceResult[7].getIndexTo(), 33);
        assertEquals(divergenceResult[8].getDivergenceType(), BEARER);
        assertEquals(divergenceResult[8].getDivergenceClass(), A_CLASS);
        assertEquals(divergenceResult[8].getIndexFrom(), 8);
        assertEquals(divergenceResult[8].getIndexTo(), 34);
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