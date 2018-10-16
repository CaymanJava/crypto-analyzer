package pro.crypto.analyzer.pvt;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.pvt.PVTResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class PVTAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testPriceVolumeTrendAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("pvt_analyzer.json", PVTAnalyzerResult[].class);
        PVTAnalyzerResult[] actualResult = new PVTAnalyzer(buildAnalyzerRequest("pvt_indicator.json", PVTResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}