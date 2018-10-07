package pro.crypto.analyzer.kvo;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.kvo.KVOResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class KVOAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testKlingerVolumeOscillatorAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("kvo_analyzer.json", KVOAnalyzerResult[].class);
        KVOAnalyzerResult[] actualResult = new KVOAnalyzer(buildAnalyzerRequest("kvo_indicator.json", KVOResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
