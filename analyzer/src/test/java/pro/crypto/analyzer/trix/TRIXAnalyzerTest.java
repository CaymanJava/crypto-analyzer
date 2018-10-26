package pro.crypto.analyzer.trix;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.trix.TRIXResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class TRIXAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testTripleExponentialAverageAnalyzerWithPeriodFourteen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("trix_analyzer_1.json", TRIXAnalyzerResult[].class);
        TRIXAnalyzerResult[] actualResult = new TRIXAnalyzer(buildAnalyzerRequest("trix_indicator_1.json", TRIXResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testTripleExponentialAverageAnalyzerWithPeriodEighteen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("trix_analyzer_2.json", TRIXAnalyzerResult[].class);
        TRIXAnalyzerResult[] actualResult = new TRIXAnalyzer(buildAnalyzerRequest("trix_indicator_2.json", TRIXResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
