package pro.crypto.analyzer.atrb;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.atrb.ATRBResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ATRBAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testAverageTrueRangeBandsAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("atrb_analyzer.json", ATRBAnalyzerResult[].class);
        ATRBAnalyzerResult[] actualResult = new ATRBAnalyzer(buildAnalyzerRequest("atrb_indicator.json", ATRBResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}