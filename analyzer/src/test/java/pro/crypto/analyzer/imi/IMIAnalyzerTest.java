package pro.crypto.analyzer.imi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.imi.IMIResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class IMIAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testIntradayMomentumIndexIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("imi_analyzer.json", IMIAnalyzerResult[].class);
        IMIAnalyzerResult[] actualResult = new IMIAnalyzer(buildAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildAnalyzerRequest() {
        return IMIAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("imi_indicator.json", IMIResult[].class))
                .oversoldLevel(30.0)
                .overboughtLevel(70.0)
                .build();
    }

}
