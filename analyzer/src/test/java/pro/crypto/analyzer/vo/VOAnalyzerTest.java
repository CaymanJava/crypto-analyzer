package pro.crypto.analyzer.vo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.vo.VOResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class VOAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testVolumeOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("vo_analyzer.json", VOAnalyzerResult[].class);
        VOAnalyzerResult[] actualResult = new VOAnalyzer(buildAnalyzerRequest("vo_indicator.json", VOResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}