package pro.crypto.analyzer.alligator;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.alligator.AlligatorResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class AlligatorAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testAlligatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("alligator_analyzer.json", AlligatorAnalyzerResult[].class);
        AlligatorAnalyzerResult[] actualResult = new AlligatorAnalyzer(buildAnalyzerRequest("alligator_indicator.json", AlligatorResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
