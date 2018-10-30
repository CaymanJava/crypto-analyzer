package pro.crypto.analyzer.wpr;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.wpr.WPRResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class WPRAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testWilliamsPercentRangeAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("wpr_analyzer.json", WPRAnalyzerResult[].class);
        WPRAnalyzerResult[] actualResult = new WPRAnalyzer(buildWPRAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildWPRAnalyzerRequest() {
        return WPRAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("wpr_indicator.json", WPRResult[].class))
                .oversoldLevel(-80.0)
                .overboughtLevel(-20.0)
                .build();
    }

}