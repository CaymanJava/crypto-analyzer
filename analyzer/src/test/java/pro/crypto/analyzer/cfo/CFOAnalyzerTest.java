package pro.crypto.analyzer.cfo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.cfo.CFOResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class CFOAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testChandeForecastOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("cfo_analyzer.json", CFOAnalyzerResult[].class);
        CFOAnalyzerResult[] actualResult = new CFOAnalyzer(buildCFOAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildCFOAnalyzerRequest() {
        return CFOAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("cfo_indicator.json", CFOResult[].class))
                .oversoldLevel(-0.5)
                .overboughtLevel(0.5)
                .build();
    }

}
