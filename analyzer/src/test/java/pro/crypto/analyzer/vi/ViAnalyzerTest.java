package pro.crypto.analyzer.vi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.vi.VIResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ViAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testNegativeVolumeIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("vi_analyzer_1.json", VIAnalyzerResult[].class);
        VIAnalyzerResult[] actualResult = new ViAnalyzer(buildAnalyzerRequest("vi_indicator_1.json", VIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testPositiveVolumeIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("vi_analyzer_2.json", VIAnalyzerResult[].class);
        VIAnalyzerResult[] actualResult = new ViAnalyzer(buildAnalyzerRequest("vi_indicator_2.json", VIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
