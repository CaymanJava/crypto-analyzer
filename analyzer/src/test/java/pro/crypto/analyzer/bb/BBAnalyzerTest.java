package pro.crypto.analyzer.bb;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.bb.BBResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class BBAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testBollingerBandsAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("bb_analyzer.json", BBAnalyzerResult[].class);
        BBAnalyzerResult[] actualResult = new BBAnalyzer(buildAnalyzerRequest("bb_indicator.json", BBResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
