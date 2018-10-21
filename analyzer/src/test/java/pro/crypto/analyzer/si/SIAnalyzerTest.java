package pro.crypto.analyzer.si;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.si.SIResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class SIAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testSwingIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("si_analyzer.json", SIAnalyzerResult[].class);
        SIAnalyzerResult[] actualResult = new SIAnalyzer(buildAnalyzerRequest("si_indicator.json", SIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
