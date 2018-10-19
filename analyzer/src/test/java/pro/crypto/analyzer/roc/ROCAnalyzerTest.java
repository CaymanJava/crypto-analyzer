package pro.crypto.analyzer.roc;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.roc.ROCResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ROCAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testRateOfChangeAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("roc_analyzer.json", ROCAnalyzerResult[].class);
        ROCAnalyzerResult[] actualResult = new ROCAnalyzer(buildAnalyzerRequest("roc_indicator.json", ROCResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
