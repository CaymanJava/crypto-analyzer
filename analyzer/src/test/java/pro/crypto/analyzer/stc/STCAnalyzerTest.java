package pro.crypto.analyzer.stc;

import org.junit.Test;
import pro.crypto.analyzer.IncreasedQuantityAnalyzerBaseTest;
import pro.crypto.indicator.stc.STCResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class STCAnalyzerTest extends IncreasedQuantityAnalyzerBaseTest {

    @Test
    public void testSchaffTrendCycleAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("stc_analyzer.json", STCAnalyzerResult[].class);
        STCAnalyzerResult[] actualResult = new STCAnalyzer(buildSTCAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildSTCAnalyzerRequest() {
        return STCAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("stc_indicator.json", STCResult[].class))
                .oversoldLevel(25.0)
                .overboughtLevel(75.0)
                .build();
    }

}