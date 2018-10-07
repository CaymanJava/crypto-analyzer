package pro.crypto.analyzer.efi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.efi.EFIResult;
import pro.crypto.model.result.AnalyzerResult;

import static org.junit.Assert.assertArrayEquals;

public class EFIAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testElderForceIndexAnalyzerWithPeriodThirteen() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("efi_analyzer_1.json", EFIAnalyzerResult[].class);
        EFIAnalyzerResult[] actualResult = new EFIAnalyzer(buildAnalyzerRequest("efi_indicator_1.json", EFIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testElderForceIndexAnalyzerWithPeriodTwo() {
        AnalyzerResult[] expectedResult = loadAnalyzerExpectedResult("efi_analyzer_2.json", EFIAnalyzerResult[].class);
        EFIAnalyzerResult[] actualResult = new EFIAnalyzer(buildAnalyzerRequest("efi_indicator_2.json", EFIResult[].class)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

}
