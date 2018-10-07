package pro.crypto.analyzer.adl;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.adl.ADLResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ADLAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testAccumulationDistributionLineAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("adl_analyzer.json", ADLAnalyzerResult[].class);
        ADLAnalyzerResult[] actualResult = new ADLAnalyzer(buildAnalyzerRequest("adl_indicator.json", ADLResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
