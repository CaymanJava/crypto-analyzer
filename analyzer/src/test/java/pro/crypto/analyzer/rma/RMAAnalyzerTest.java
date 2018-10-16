package pro.crypto.analyzer.rma;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.rma.RMAResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class RMAAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testRainbowMovingAverageAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("rma_analyzer.json", RMAAnalyzerResult[].class);
        RMAAnalyzerResult[] actualResult = new RMAAnalyzer(buildAnalyzerRequest("rma_indicator.json", RMAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}