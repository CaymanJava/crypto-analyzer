package pro.crypto.analyzer.cc;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.cc.CCResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class CCAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testCoppockCurveAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("cc_analyzer.json", CCAnalyzerResult[].class);
        CCAnalyzerResult[] actualResult = new CCAnalyzer(buildAnalyzerRequest("cc_indicator.json", CCResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
