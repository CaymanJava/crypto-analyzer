package pro.crypto.analyzer.ro;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.ro.ROResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class ROAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testRainbowOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ro_analyzer.json", ROAnalyzerResult[].class);
        ROAnalyzerResult[] actualResult = new ROAnalyzer(buildROAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildROAnalyzerRequest() {
        return ROAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("ro_indicator.json", ROResult[].class))
                .minUptrendEnvelopeLevel(30.0)
                .maxUptrendEnvelopeLevel(60.0)
                .acceptableSignalEnvelopeLevel(38.0)
                .build();
    }

}
