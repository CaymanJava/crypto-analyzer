package pro.crypto.analyzer.smi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.smi.SMIResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class SMIAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testStochasticMomentumIndexAnalyzerWithTenAndThreePeriods() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("smi_analyzer_1.json", SMIAnalyzerResult[].class);
        SMIAnalyzerResult[] actualResult = new SMIAnalyzer(buildSMIAnalyzerRequest("smi_indicator_1.json")).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testStochasticMomentumIndexAnalyzerWithFourteenAndFourPeriods() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("smi_analyzer_2.json", SMIAnalyzerResult[].class);
        SMIAnalyzerResult[] actualResult = new SMIAnalyzer(buildSMIAnalyzerRequest("smi_indicator_2.json")).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildSMIAnalyzerRequest(String fileName) {
        return SMIAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult(fileName, SMIResult[].class))
                .oversoldLevel(-40.0)
                .overboughtLevel(40.0)
                .build();
    }

}
