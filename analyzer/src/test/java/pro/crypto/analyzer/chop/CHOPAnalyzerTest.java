package pro.crypto.analyzer.chop;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.chop.CHOPResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class CHOPAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testChoppinessIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("chop_analyzer.json", CHOPAnalyzerResult[].class);
        CHOPAnalyzerResult[] actualResult = new CHOPAnalyzer(buildCHOPAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildCHOPAnalyzerRequest() {
        return CHOPAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("chop_indicator.json", CHOPResult[].class))
                .lowerTrendLine(38.2)
                .upperTrendLine(61.8)
                .build();
    }

}
