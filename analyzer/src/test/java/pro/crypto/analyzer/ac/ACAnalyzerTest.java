package pro.crypto.analyzer.ac;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.ac.ACResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ACAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testAccelerationDecelerationOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ac_analyzer.json", ACAnalyzerResult[].class);
        ACAnalyzerResult[] actualResult = new ACAnalyzer(buildAnalyzerRequest("ac_indicator.json", ACResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}