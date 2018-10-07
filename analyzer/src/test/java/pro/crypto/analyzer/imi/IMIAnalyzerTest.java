package pro.crypto.analyzer.imi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.imi.IMIResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class IMIAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testIntradayMomentumIndexIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("imi_analyzer.json", IMIAnalyzerResult[].class);
        IMIAnalyzerResult[] actualResult = new IMIAnalyzer(buildAnalyzerRequest("imi_indicator.json", IMIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
