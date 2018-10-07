package pro.crypto.analyzer.co;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.co.COResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class COAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testChaikinOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("co_analyzer.json", COAnalyzerResult[].class);
        COAnalyzerResult[] actualResult = new COAnalyzer(buildAnalyzerRequest("co_indicator.json", COResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
