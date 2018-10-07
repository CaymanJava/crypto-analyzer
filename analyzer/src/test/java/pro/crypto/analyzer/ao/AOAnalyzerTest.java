package pro.crypto.analyzer.ao;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.ao.AOResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class AOAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testAccelerationDecelerationOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ao_analyzer.json", AOAnalyzerResult[].class);
        AOAnalyzerResult[] actualResult = new AOAnalyzer(buildAnalyzerRequest("ao_indicator.json", AOResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}