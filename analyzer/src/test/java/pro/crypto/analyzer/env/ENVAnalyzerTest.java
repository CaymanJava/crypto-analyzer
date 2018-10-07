package pro.crypto.analyzer.env;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.env.ENVResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ENVAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testMovingAverageEnvelopesAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("env_analyzer.json", ENVAnalyzerResult[].class);
        ENVAnalyzerResult[] actualResult = new ENVAnalyzer(buildAnalyzerRequest("env_indicator.json", ENVResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
