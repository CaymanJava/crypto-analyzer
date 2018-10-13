package pro.crypto.analyzer.pmo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.pmo.PMOResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class PMOAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testPriceMomentumOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("pmo_analyzer.json", PMOAnalyzerResult[].class);
        PMOAnalyzerResult[] actualResult = new PMOAnalyzer(buildAnalyzerRequest("pmo_indicator.json", PMOResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}