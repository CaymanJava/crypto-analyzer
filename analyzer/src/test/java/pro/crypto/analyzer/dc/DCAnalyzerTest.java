package pro.crypto.analyzer.dc;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.dc.DCResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class DCAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testDonchianChannelAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("dc_analyzer.json", DCAnalyzerResult[].class);
        DCAnalyzerResult[] actualResult = new DCAnalyzer(buildAnalyzerRequest("dc_indicator.json", DCResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
