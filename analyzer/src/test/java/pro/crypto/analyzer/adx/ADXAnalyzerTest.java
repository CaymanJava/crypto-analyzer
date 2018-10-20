package pro.crypto.analyzer.adx;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.adx.ADXResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ADXAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testAverageDirectionalMovementIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("adx_analyzer.json", ADXAnalyzerResult[].class);
        ADXAnalyzerResult[] actualResult = new ADXAnalyzer(buildADXAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildADXAnalyzerRequest() {
        return ADXAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("adx_indicator.json", ADXResult[].class))
                .weakTrendLine(20.0)
                .strongTrendLine(40.0)
                .build();
    }

}
