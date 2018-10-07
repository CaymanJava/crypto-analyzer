package pro.crypto.analyzer.eri;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.eri.ERIResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ERIAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testEhlersFisherTransformAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("eri_analyzer.json", ERIAnalyzerResult[].class);
        ERIAnalyzerResult[] actualResult = new ERIAnalyzer(buildAnalyzerRequest("eri_indicator.json", ERIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
