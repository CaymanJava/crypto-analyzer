package pro.crypto.analyzer.mfi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerBaseTest;
import pro.crypto.indicator.mfi.MFIResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class MFIAnalyzerTest extends AnalyzerBaseTest {

    @Test
    public void testMarketFacilitationIndexAnalyzer() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("mfi_analyzer.json", MFIAnalyzerResult[].class);
        MFIAnalyzerResult[] actualResult = new MFIAnalyzer(buildAnalyzerRequest("mfi_indicator.json", MFIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}