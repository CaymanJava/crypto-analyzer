package pro.crypto.analyzer.qs;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.qs.QSResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class QSAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testQuickStickAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("qs_analyzer.json", QSAnalyzerResult[].class);
        QSAnalyzerResult[] actualResult = new QSAnalyzer(buildAnalyzerRequest("qs_indicator.json", QSResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}