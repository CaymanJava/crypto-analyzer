package pro.crypto.analyzer.ma;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class MAAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testDisplacedSimpleMovingAverageAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_1.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_1.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDisplacedExponentialMovingAverageAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_2.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_2.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDisplacedHullMovingAverageAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_3.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_3.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDisplacedSmoothedMovingAverageAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_4.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_4.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDisplacedWeightedMovingAverageAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_5.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_5.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDoubleExponentialMovingAverageAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_6.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_6.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testExponentialMovingAverageAnalyzerWithPeriodFifteen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_7.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_7.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testExponentialMovingAverageAnalyzerWithPeriodTwenty() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_8.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_8.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testExponentialMovingAverageAnalyzerWithAlphaCoefficient() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_9.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_9.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testHullMovingAverageAnalyzerWithPeriodFifteen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_10.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_10.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testHullMovingAverageAnalyzerWithPeriodTwenty() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_11.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_11.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }


    @Test
    public void testKaufmanAdaptiveMovingAverageAnalyzerWithPeriodTen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_12.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_12.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testModifiedMovingAverageAnalyzerWithPeriodFourteen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_13.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_13.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testSimpleMovingAverageAnalyzerWithPeriodFifteen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_14.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_14.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testSimpleMovingAnalyzerAverageWithPeriodTwenty() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_15.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_15.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testSmoothedMovingAverageAnalyzerWithPeriodFifteen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_16.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_16.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testSmoothedMovingAverageAnalyzerWithPeriodTwenty() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_17.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_17.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testTimeSeriesMovingAverageAnalyzerWithPeriodFifteen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_18.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_18.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testTimeSeriesMovingAverageAnalyzerWithPeriodTwenty() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_19.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_19.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testTriangularMovingAverageAnalyzerWithEvenPeriod() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_20.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_20.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testTriangularMovingAverageAnalyzerWithOddPeriod() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_21.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_21.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testVariableIndexDynamicAverageAnalyzerWithPeriodTen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_22.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_22.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testWeightedMovingAverageAnalyzerWithPeriodFifteen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_23.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_23.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testWeightedMovingAverageAnalyzerWithPeriodTwenty() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_24.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_24.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testWellesWildersMovingAverageAnalyzerWithPeriodFifteen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_25.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_25.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testWellesWildersMovingAverageAnalyzerWithPeriodTwenty() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_26.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_26.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testTripleExponentialMovingAverageAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ma_analyzer_27.json", MAAnalyzerResult[].class);
        MAAnalyzerResult[] actualResult = new MAAnalyzer(buildAnalyzerRequest("ma_indicator_27.json", MAResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}