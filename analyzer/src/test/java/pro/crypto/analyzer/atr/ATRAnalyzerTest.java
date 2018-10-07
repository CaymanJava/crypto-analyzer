package pro.crypto.analyzer.atr;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.atr.ATRResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ATRAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testAverageTrueRangeAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("atr_analyzer.json", ATRAnalyzerResult[].class);
        ATRAnalyzerResult[] actualResult = new ATRAnalyzer(buildAnalyzerRequest("atr_indicator.json", ATRResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}