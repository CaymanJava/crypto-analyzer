package pro.crypto.analyzer.rv;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.rv.RVResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class RVAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testRelativeVolatilityAnalyzerWithSingleLine() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("rv_analyzer_1.json", RVAnalyzerResult[].class);
        RVAnalyzerResult[] actualResult = new RVAnalyzer(buildRVAnalyzerRequest("rv_indicator_1.json", 50.0, 50.0)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testRelativeVolatilityAnalyzerWithDoubleLine() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("rv_analyzer_2.json", RVAnalyzerResult[].class);
        RVAnalyzerResult[] actualResult = new RVAnalyzer(buildRVAnalyzerRequest("rv_indicator_2.json", 40.0, 60.0)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildRVAnalyzerRequest(String fileName, double bullishSignalLine, double bearerSignalLine) {
        return RVAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult(fileName, RVResult[].class))
                .bullishSignalLine(bullishSignalLine)
                .bearerSignalLine(bearerSignalLine)
                .build();
    }

}