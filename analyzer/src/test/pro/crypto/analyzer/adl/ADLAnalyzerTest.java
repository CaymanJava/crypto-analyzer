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
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.NEUTRAL;
import static pro.crypto.model.Signal.SELL;

public class ADLAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAccumulationDistributionLineAnalyzer() {
        ADLResult[] indicatorResult = new AccumulationDistributionLine(buildIndicatorRequest()).getResult();
        ADLAnalyzerResult[] analyzerResults = new ADLAnalyzer(buildAnalyzerResult(indicatorResult)).getResult();
        assertTrue(analyzerResults.length == originalData.length);
        assertEquals(analyzerResults[31].getIndicatorValue(), toBigDecimal(-431.100481141));
        assertEquals(analyzerResults[31].getSignal(), SELL);
        assertEquals(analyzerResults[35].getIndicatorValue(), toBigDecimal(-163.7707684989));
        assertEquals(analyzerResults[35].getSignal(), SELL);
        assertEquals(analyzerResults[37].getIndicatorValue(), toBigDecimal(-170.2435303218));
        assertEquals(analyzerResults[37].getSignal(), SELL);
        assertEquals(analyzerResults[38].getIndicatorValue(), toBigDecimal(-68.2995968351));
        assertEquals(analyzerResults[38].getSignal(), BUY);
        assertEquals(analyzerResults[39].getIndicatorValue(), toBigDecimal(-186.5266594385));
        assertEquals(analyzerResults[39].getSignal(), SELL);
        assertEquals(analyzerResults[72].getIndicatorValue(), toBigDecimal(347.8292776260));
        assertEquals(analyzerResults[72].getSignal(), NEUTRAL);
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