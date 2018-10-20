package pro.crypto.analyzer.cci;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.cci.CCIResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class CCIAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testCommodityChannelIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("cci_analyzer.json", CCIAnalyzerResult[].class);
        CCIAnalyzerResult[] actualResult = new CCIAnalyzer(buildCCIAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildCCIAnalyzerRequest() {
        return CCIAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("cci_indicator.json", CCIResult[].class))
                .oversoldLevel(-100.0)
                .overboughtLevel(100.0)
                .build();
    }

}
