package pro.crypto.analyzer.cmf;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.cmf.CMFResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class CMFAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testChaikinMoneyFlowAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("cmf_analyzer.json", CMFAnalyzerResult[].class);
        CMFAnalyzerResult[] actualResult = new CMFAnalyzer(buildCMFAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildCMFAnalyzerRequest() {
        return CMFAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("cmf_indicator.json", CMFResult[].class))
                .bearerSignalLine(-0.05)
                .bullishSignalLine(0.05)
                .build();
    }

}
