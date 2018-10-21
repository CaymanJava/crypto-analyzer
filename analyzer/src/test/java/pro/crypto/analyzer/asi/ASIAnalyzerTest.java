package pro.crypto.analyzer.asi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.asi.ASIResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ASIAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testAccumulativeSwingIndexAnalyzerWithLimitHalf() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("asi_analyzer.json", ASIAnalyzerResult[].class);
        ASIAnalyzerResult[] actualResult = new ASIAnalyzer(buildAnalyzerRequest("asi_indicator.json", ASIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}