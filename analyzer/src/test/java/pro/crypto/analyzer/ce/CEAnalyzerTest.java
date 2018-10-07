package pro.crypto.analyzer.ce;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.ce.CEResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class CEAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testChandelierExitAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ce_analyzer.json", CEAnalyzerResult[].class);
        CEAnalyzerResult[] actualResult = new CEAnalyzer(buildAnalyzerRequest("ce_indicator.json", CEResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
