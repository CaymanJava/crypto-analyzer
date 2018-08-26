package pro.crypto.analyzer.adl;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.adl.ADLRequest;
import pro.crypto.indicator.adl.ADLResult;
import pro.crypto.indicator.adl.AccumulationDistributionLine;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static pro.crypto.model.Signal.*;

public class ADLAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAccumulationDistributionLineAnalyzer() {
        ADLResult[] indicatorResult = new AccumulationDistributionLine(buildIndicatorRequest()).getResult();
        ADLAnalyzerResult[] result = new ADLAnalyzer(buildAnalyzerResult(indicatorResult)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getSignal(), SELL);
        assertEquals(result[35].getTime(), of(2018, 4, 1, 0, 0));
        assertEquals(result[35].getSignal(), SELL);
        assertEquals(result[37].getTime(), of(2018, 4, 3, 0, 0));
        assertEquals(result[37].getSignal(), SELL);
        assertEquals(result[38].getTime(), of(2018, 4, 4, 0, 0));
        assertEquals(result[38].getSignal(), BUY);
        assertEquals(result[39].getTime(), of(2018, 4, 5, 0, 0));
        assertEquals(result[39].getSignal(), SELL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    private AnalyzerRequest buildAnalyzerResult(ADLResult[] indicatorResult) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResult)
                .build();
    }

    private IndicatorRequest buildIndicatorRequest() {
        return ADLRequest.builder()
                .originalData(originalData)
                .build();
    }

}