package pro.crypto.analyzer.dpo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.dpo.DPOResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class DPOAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testDetrendedPriceOscillatorAnalyzerWithPeriodSeven() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("dpo_analyzer_1.json", DPOAnalyzerResult[].class);
        DPOAnalyzerResult[] actualResult = new DPOAnalyzer(buildAnalyzerRequest("dpo_indicator_1.json", DPOResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDetrendedPriceOscillatorAnalyzerWithPeriodTen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("dpo_analyzer_2.json", DPOAnalyzerResult[].class);
        DPOAnalyzerResult[] actualResult = new DPOAnalyzer(buildAnalyzerRequest("dpo_indicator_2.json", DPOResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
