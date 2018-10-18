package pro.crypto.analyzer.ro;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.ro.ROResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ROAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testRainbowMovingAverageAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ro_analyzer.json", ROAnalyzerResult[].class);
        ROAnalyzerResult[] actualResult = new ROAnalyzer(buildAnalyzerRequest("ro_indicator.json", ROResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}