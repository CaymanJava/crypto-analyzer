package pro.crypto.analyzer.bbw;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.bbw.BBWResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class BBWAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testBollingerBandsWidthAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("bbw_analyzer.json", BBWAnalyzerResult[].class);
        BBWAnalyzerResult[] actualResult = new BBWAnalyzer(buildAnalyzerRequest("bbw_indicator.json", BBWResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
