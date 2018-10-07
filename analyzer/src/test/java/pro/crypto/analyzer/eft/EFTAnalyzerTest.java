package pro.crypto.analyzer.eft;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.eft.EFTResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class EFTAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testEhlersFisherTransformAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("eft_analyzer.json", EFTAnalyzerResult[].class);
        EFTAnalyzerResult[] actualResult = new EFTAnalyzer(buildAnalyzerRequest("eft_indicator.json", EFTResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
