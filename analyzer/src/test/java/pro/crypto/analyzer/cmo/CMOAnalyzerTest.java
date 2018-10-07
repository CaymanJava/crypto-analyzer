package pro.crypto.analyzer.cmo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.cmo.CMOResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class CMOAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testChandeMomentumOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("cmo_analyzer.json", CMOAnalyzerResult[].class);
        CMOAnalyzerResult[] actualResult = new CMOAnalyzer(buildAnalyzerRequest("cmo_indicator.json", CMOResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
