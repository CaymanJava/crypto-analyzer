package pro.crypto.analyzer.di;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.di.DIResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class DIAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testDisparityIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("di_analyzer.json", DIAnalyzerResult[].class);
        DIAnalyzerResult[] actualResult = new DIAnalyzer(buildAnalyzerRequest("di_indicator.json", DIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
