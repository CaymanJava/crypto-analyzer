package pro.crypto.analyzer.rsi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.rsi.RSIResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class RSIAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testConnorsRelativeStrengthIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("rsi_analyzer_1.json", RSIAnalyzerResult[].class);
        RSIAnalyzerResult[] actualResult = new RSIAnalyzer(buildRSIAnalyzerRequest("rsi_indicator_1.json", 90.0, 10.0)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testRelativeStrengthIndexWithSmoothedMovingAverageAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("rsi_analyzer_2.json", RSIAnalyzerResult[].class);
        RSIAnalyzerResult[] actualResult = new RSIAnalyzer(buildRSIAnalyzerRequest("rsi_indicator_2.json", 65.0, 35.0)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testRelativeStrengthIndexWithExponentialMovingAverageAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("rsi_analyzer_3.json", RSIAnalyzerResult[].class);
        RSIAnalyzerResult[] actualResult = new RSIAnalyzer(buildRSIAnalyzerRequest("rsi_indicator_3.json", 70.0, 30.0)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testStochasticRelativeStrengthIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("rsi_analyzer_4.json", RSIAnalyzerResult[].class);
        RSIAnalyzerResult[] actualResult = new RSIAnalyzer(buildRSIAnalyzerRequest("rsi_indicator_4.json", 0.8, 0.2)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildRSIAnalyzerRequest(String fileName, double overboughtLevel, double oversoldLevel) {
        return RSIAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult(fileName, RSIResult[].class))
                .overboughtLevel(overboughtLevel)
                .oversoldLevel(oversoldLevel)
                .build();
    }

}