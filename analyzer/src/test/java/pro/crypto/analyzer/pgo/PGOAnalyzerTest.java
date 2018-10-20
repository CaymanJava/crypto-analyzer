package pro.crypto.analyzer.pgo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.pgo.PGOResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class PGOAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testPrettyGoodOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("pgo_analyzer.json", PGOAnalyzerResult[].class);
        PGOAnalyzerResult[] actualResult = new PGOAnalyzer(buildPGOAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildPGOAnalyzerRequest() {
        return PGOAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("pgo_indicator.json", PGOResult[].class))
                .oversoldLevel(-3.0)
                .overboughtLevel(3.0)
                .build();
    }

}
