package pro.crypto.analyzer.fractal;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.fractal.FractalResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class FractalAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testFractalAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("fractal_analyzer.json", FractalAnalyzerResult[].class);
        FractalAnalyzerResult[] actualResult = new FractalAnalyzer(buildAnalyzerRequest("fractal_indicator.json", FractalResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
