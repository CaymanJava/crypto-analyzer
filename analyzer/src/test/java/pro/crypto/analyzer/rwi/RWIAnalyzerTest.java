package pro.crypto.analyzer.rwi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.rwi.RWIResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class RWIAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testRandomWalkIndexAnalyzerWithPeriodTen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("rwi_analyzer_1.json", RWIAnalyzerResult[].class);
        RWIAnalyzerResult[] actualResult = new RWIAnalyzer(buildAnalyzerRequest("rwi_indicator_1.json", RWIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testRandomWalkIndexAnalyzerWithPeriodFourteen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("rwi_analyzer_2.json", RWIAnalyzerResult[].class);
        RWIAnalyzerResult[] actualResult = new RWIAnalyzer(buildAnalyzerRequest("rwi_indicator_2.json", RWIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}