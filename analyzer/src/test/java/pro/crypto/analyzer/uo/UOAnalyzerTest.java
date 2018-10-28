package pro.crypto.analyzer.uo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.uo.UOResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class UOAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testUltimateOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("uo_analyzer.json", UOAnalyzerResult[].class);
        UOAnalyzerResult[] actualResult = new UOAnalyzer(buildUOAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildUOAnalyzerRequest() {
        return UOAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("uo_indicator.json", UOResult[].class))
                .oversoldLevel(30.0)
                .overboughtLevel(70.0)
                .build();
    }

}
