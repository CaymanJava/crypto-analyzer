package pro.crypto.analyzer.ic;

import org.junit.Test;
import pro.crypto.analyzer.IncreasedQuantityAnalyzerBaseTest;
import pro.crypto.indicator.ic.ICResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.analyzer.ic.ICDataGenerator.*;

public class ICAnalyzerTest extends IncreasedQuantityAnalyzerBaseTest {

    @Test
    public void testIchimokuCloudsAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ic_analyzer_1.json", ICAnalyzerResult[].class);
        ICAnalyzerResult[] actualResult = new ICAnalyzer(buildAnalyzerRequest("ic_indicator.json", ICResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testTenkanKijunCross() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ic_analyzer_2.json", ICAnalyzerResult[].class);
        ICAnalyzerResult[] actualResult = new ICAnalyzer(generateDataForTenkanKijunCrossTest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testPriceKijunCross() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ic_analyzer_3.json", ICAnalyzerResult[].class);
        ICAnalyzerResult[] actualResult = new ICAnalyzer(generateDataForPriceKijunCrossSignals()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testPriceCloudCross() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ic_analyzer_4.json", ICAnalyzerResult[].class);
        ICAnalyzerResult[] actualResult = new ICAnalyzer(generateDataForPriceCloudCross()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
