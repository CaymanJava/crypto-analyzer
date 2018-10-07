package pro.crypto.analyzer.ac;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.ac.ACResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ACAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testAccelerationDecelerationOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ac_analyzer.json", ACAnalyzeResult[].class);
        ACAnalyzeResult[] actualResult = new ACAnalyzer(buildAnalyzerRequest("ac_indicator.json", ACResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}