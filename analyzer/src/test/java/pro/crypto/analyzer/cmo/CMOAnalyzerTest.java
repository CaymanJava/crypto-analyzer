package pro.crypto.analyzer.cmo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.cmo.CMOResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class CMOAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testChandeMomentumOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("cmo_analyzer.json", CMOAnalyzerResult[].class);
        CMOAnalyzerResult[] actualResult = new CMOAnalyzer(buildCMOAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildCMOAnalyzerRequest() {
        return CMOAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("cmo_indicator.json", CMOResult[].class))
                .oversoldLevel(-50.0)
                .overboughtLevel(50.0)
                .build();
    }

}
