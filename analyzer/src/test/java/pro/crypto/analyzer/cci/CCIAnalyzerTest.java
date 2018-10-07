package pro.crypto.analyzer.cci;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.cci.CCIResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class CCIAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testCommodityChannelIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("cci_analyzer.json", CCIAnalyzerResult[].class);
        CCIAnalyzerResult[] actualResult = new CCIAnalyzer(buildAnalyzerRequest("cci_indicator.json", CCIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
