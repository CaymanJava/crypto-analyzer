package pro.crypto.analyzer.adl;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.adl.ADLRequest;
import pro.crypto.indicator.adl.ADLResult;
import pro.crypto.indicator.adl.AccumulationDistributionLine;
import pro.crypto.model.IndicatorRequest;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static pro.crypto.model.Signal.*;

public class ADLAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testAccumulationDistributionLineAnalyzer() {
        ADLResult[] indicatorResult = new AccumulationDistributionLine(buildIndicatorRequest()).getResult();
        ADLAnalyzerResult[] result = new ADLAnalyzer(buildAnalyzerRequest(indicatorResult)).getResult();
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

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return ADLRequest.builder()
                .originalData(originalData)
                .build();
    }

}
