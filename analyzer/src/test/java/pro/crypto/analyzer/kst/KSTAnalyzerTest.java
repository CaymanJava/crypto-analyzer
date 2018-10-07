package pro.crypto.analyzer.kst;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.kst.KSTResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class KSTAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testKnowSureThingAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("kst_analyzer.json", KSTAnalyzerResult[].class);
        KSTAnalyzerResult[] actualResult = new KSTAnalyzer(buildAnalyzerRequest("kst_indicator.json", KSTResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
