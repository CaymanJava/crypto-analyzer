package pro.crypto.analyzer.psar;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.psar.PSARResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class PSARAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testParabolicStopAndReverseAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("psar_analyzer.json", PSARAnalyzerResult[].class);
        PSARAnalyzerResult[] actualResult = new PSARAnalyzer(buildAnalyzerRequest("psar_indicator.json", PSARResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
