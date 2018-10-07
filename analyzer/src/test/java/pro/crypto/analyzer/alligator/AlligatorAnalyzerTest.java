package pro.crypto.analyzer.alligator;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.alligator.AlligatorResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class AlligatorAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testAlligatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("alligator_analyzer.json", AlligatorAnalyzerResult[].class);
        AlligatorAnalyzerResult[] actualResult = new AlligatorAnalyzer(buildAnalyzerRequest("alligator_indicator.json", AlligatorResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
