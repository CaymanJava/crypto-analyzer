package pro.crypto.analyzer.mi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.mi.MIResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class MIAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testMassIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("mi_analyzer.json", MIAnalyzerResult[].class);
        MIAnalyzerResult[] actualResult = new MIAnalyzer(buildMIAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildMIAnalyzerRequest() {
        return MIAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("mi_indicator.json", MIResult[].class))
                .firstReversalLine(27.0)
                .secondReversalLine(26.5)
                .allowableGap(25)
                .build();
    }

}