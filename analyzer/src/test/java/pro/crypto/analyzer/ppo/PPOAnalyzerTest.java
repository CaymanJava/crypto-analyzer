package pro.crypto.analyzer.ppo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.ppo.PPOResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class PPOAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testPercentagePriceOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("ppo_analyzer.json", PPOAnalyzerResult[].class);
        PPOAnalyzerResult[] actualResult = new PPOAnalyzer(buildAnalyzerRequest("ppo_indicator.json", PPOResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}