package pro.crypto.analyzer.pgo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.pgo.PGOResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class PGOAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testPrettyGoodOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("pgo_analyzer.json", PGOAnalyzerResult[].class);
        PGOAnalyzerResult[] actualResult = new PGOAnalyzer(buildAnalyzerRequest("pgo_indicator.json", PGOResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}