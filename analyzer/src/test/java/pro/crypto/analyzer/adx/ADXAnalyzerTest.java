package pro.crypto.analyzer.adx;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.adx.ADXResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ADXAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testAverageDirectionalMovementIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("adx_analyzer.json", ADXAnalyzerResult[].class);
        ADXAnalyzerResult[] actualResult = new ADXAnalyzer(buildAnalyzerRequest("adx_indicator.json", ADXResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
