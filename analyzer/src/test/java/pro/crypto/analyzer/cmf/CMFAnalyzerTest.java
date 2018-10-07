package pro.crypto.analyzer.cmf;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.cmf.CMFResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class CMFAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testChaikinMoneyFlowAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("cmf_analyzer.json", CMFAnalyzerResult[].class);
        CMFAnalyzerResult[] actualResult = new CMFAnalyzer(buildAnalyzerRequest("cmf_indicator.json", CMFResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
