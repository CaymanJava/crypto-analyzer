package pro.crypto.analyzer.st;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.st.STResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class STAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testSuperTrendAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("st_analyzer.json", STAnalyzerResult[].class);
        STAnalyzerResult[] actualResult = new STAnalyzer(buildAnalyzerRequest("st_indicator.json", STResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}