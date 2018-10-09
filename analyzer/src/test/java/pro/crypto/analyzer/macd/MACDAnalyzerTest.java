package pro.crypto.analyzer.macd;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.macd.MACDResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class MACDAnalyzerTest extends AnalyzerBaseTest{

    @Test
    public void testMovingAverageConvergenceDivergenceAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("macd_analyzer.json", MACDAnalyzerResult[].class);
        MACDAnalyzerResult[] actualResult = new MACDAnalyzer(buildAnalyzerRequest("macd_indicator.json", MACDResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}