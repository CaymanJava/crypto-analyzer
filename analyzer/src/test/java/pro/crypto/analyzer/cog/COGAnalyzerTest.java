package pro.crypto.analyzer.cog;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.cog.COGResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class COGAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testCenterOfGravityAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("cog_analyzer.json", COGAnalyzerResult[].class);
        COGAnalyzerResult[] actualResult = new COGAnalyzer(buildAnalyzerRequest("cog_indicator.json", COGResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
