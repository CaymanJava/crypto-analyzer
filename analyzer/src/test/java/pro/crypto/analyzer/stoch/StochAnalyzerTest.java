package pro.crypto.analyzer.stoch;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.stoch.StochResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class StochAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testStochasticOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("stoch_analyzer_1.json", StochAnalyzerResult[].class);
        StochAnalyzerResult[] actualResult = new StochAnalyzer(buildStochAnalyzerRequest("stoch_indicator_1.json")).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testPreferableStochasticOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("stoch_analyzer_2.json", StochAnalyzerResult[].class);
        StochAnalyzerResult[] actualResult = new StochAnalyzer(buildStochAnalyzerRequest("stoch_indicator_2.json")).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildStochAnalyzerRequest(String fileName) {
        return StochAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult(fileName, StochResult[].class))
                .oversoldLevel(20.0)
                .overboughtLevel(80.0)
                .build();
    }

}