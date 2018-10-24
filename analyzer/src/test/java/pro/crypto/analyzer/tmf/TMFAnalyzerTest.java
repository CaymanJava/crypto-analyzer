package pro.crypto.analyzer.tmf;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.tmf.TMFResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class TMFAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testTwiggsMoneyFlowAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("tmf_analyzer.json", TMFAnalyzerResult[].class);
        TMFAnalyzerResult[] actualResult = new TMFAnalyzer(buildAnalyzerRequest("tmf_indicator.json", TMFResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}