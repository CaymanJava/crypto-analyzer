package pro.crypto.analyzer.aroon;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.aroon.AroonResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class AroonAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testAroonAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("aroon_analyzer.json", AroonAnalyzerResult[].class);
        AroonAnalyzerResult[] actualResult = new AroonAnalyzer(buildAroonAnalyzerRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private AnalyzerRequest buildAroonAnalyzerRequest() {
        return AroonAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(loadIndicatorResult("aroon_indicator.json", AroonResult[].class))
                .weakTrendLine(30.0)
                .normalTrendLine(50.0)
                .strongTrendLine(70.0)
                .build();
    }

}