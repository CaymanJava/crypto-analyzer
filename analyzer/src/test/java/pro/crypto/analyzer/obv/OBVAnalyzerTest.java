package pro.crypto.analyzer.obv;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.obv.OBVResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class OBVAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testOnBalanceVolumeAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("obv_analyzer.json", OBVAnalyzerResult[].class);
        OBVAnalyzerResult[] actualResult = new OBVAnalyzer(buildAnalyzerRequest("obv_indicator.json", OBVResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}