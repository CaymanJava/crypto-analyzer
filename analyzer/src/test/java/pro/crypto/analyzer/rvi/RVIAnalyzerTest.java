package pro.crypto.analyzer.rvi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.rvi.RVIResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class RVIAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testRelativeVigorIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("rvi_analyzer.json", RVIAnalyzerResult[].class);
        RVIAnalyzerResult[] actualResult = new RVIAnalyzer(buildAnalyzerRequest("rvi_indicator.json", RVIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}