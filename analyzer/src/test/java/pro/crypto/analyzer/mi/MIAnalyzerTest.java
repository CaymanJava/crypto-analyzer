package pro.crypto.analyzer.mi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.mi.MIResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class MIAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testMassIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("mi_analyzer.json", MIAnalyzerResult[].class);
        MIAnalyzerResult[] actualResult = new MIAnalyzer(buildAnalyzerRequest("mi_indicator.json", MIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}