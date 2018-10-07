package pro.crypto.analyzer.chop;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.chop.CHOPResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class CHOPAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testChoppinessIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("chop_analyzer.json", CHOPAnalyzerResult[].class);
        CHOPAnalyzerResult[] actualResult = new CHOPAnalyzer(buildAnalyzerRequest("chop_indicator.json", CHOPResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
