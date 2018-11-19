package pro.crypto.analyzer.eis;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.eis.EISResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class EISAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testElderImpulseSystemAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("eis_analyzer.json", EISAnalyzerResult[].class);
        EISAnalyzerResult[] actualResult = new EISAnalyzer(buildAnalyzerRequest("eis_indicator.json", EISResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}