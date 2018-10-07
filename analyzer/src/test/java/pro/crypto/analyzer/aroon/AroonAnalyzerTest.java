package pro.crypto.analyzer.aroon;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.aroon.AroonResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class AroonAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testAroonAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("aroon_analyzer.json", AroonAnalyzerResult[].class);
        AroonAnalyzerResult[] actualResult = new AroonAnalyzer(buildAnalyzerRequest("aroon_indicator.json", AroonResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}