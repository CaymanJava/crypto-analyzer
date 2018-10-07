package pro.crypto.analyzer.kelt;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.kelt.KELTResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class KELTAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testKeltnerChannelAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("kelt_analyzer.json", KELTAnalyzerResult[].class);
        KELTAnalyzerResult[] actualResult = new KELTAnalyzer(buildAnalyzerRequest("kelt_indicator.json", KELTResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
