package pro.crypto.analyzer.eom;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.eom.EOMResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class EOMAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testEaseOfMovementAnalyzerWithPeriodFourteen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("eom_analyzer.json", EOMAnalyzerResult[].class);
        EOMAnalyzerResult[] actualResult = new EOMAnalyzer(buildAnalyzerRequest("eom_indicator.json", EOMResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
