package pro.crypto.analyzer.hlb;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.hlb.HLBResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class HLBAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testHighLowBandsAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("hlb_analyzer.json", HLBAnalyzerResult[].class);
        HLBAnalyzerResult[] actualResult = new HLBAnalyzer(buildAnalyzerRequest("hlb_indicator.json", HLBResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
