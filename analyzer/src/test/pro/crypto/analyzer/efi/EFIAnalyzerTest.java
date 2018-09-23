package pro.crypto.analyzer.efi;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.efi.EFIRequest;
import pro.crypto.indicator.efi.ElderForceIndex;
import pro.crypto.model.IndicatorResult;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.Signal.*;

public class EFIAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testElderForceIndexAnalyzerWithPeriodThirteen() {
        EFIRequest indicatorRequest = buildIndicatorRequest();
        indicatorRequest.setPeriod(13);
        IndicatorResult[] indicatorResults = new ElderForceIndex(indicatorRequest).getResult();
        EFIAnalyzerResult[] result = new EFIAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertEquals(result[20].getSignal(), BUY);
        assertEquals(result[21].getTime(), of(2018, 3, 18, 0, 0));
        assertEquals(result[21].getSignal(), SELL);
        assertEquals(result[27].getTime(), of(2018, 3, 24, 0, 0));
        assertEquals(result[27].getSignal(), BUY);
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getSignal(), SELL);
        assertEquals(result[50].getTime(), of(2018, 4, 16, 0, 0));
        assertEquals(result[50].getSignal(), BUY);
        assertEquals(result[55].getTime(), of(2018, 4, 21, 0, 0));
        assertEquals(result[55].getSignal(), SELL);
        assertEquals(result[56].getTime(), of(2018, 4, 22, 0, 0));
        assertEquals(result[56].getSignal(), BUY);
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getSignal(), SELL);
        assertEquals(result[69].getTime(), of(2018, 5, 5, 0, 0));
        assertEquals(result[69].getSignal(), BUY);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    @Test
    public void testElderForceIndexAnalyzerWithPeriodTwo() {
        EFIRequest indicatorRequest = buildIndicatorRequest();
        indicatorRequest.setPeriod(2);
        IndicatorResult[] indicatorResults = new ElderForceIndex(indicatorRequest).getResult();
        EFIAnalyzerResult[] result = new EFIAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[7].getTime(), of(2018, 3, 4, 0, 0));
        assertEquals(result[7].getSignal(), SELL);
        assertEquals(result[16].getTime(), of(2018, 3, 13, 0, 0));
        assertEquals(result[16].getSignal(), BUY);
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertEquals(result[20].getSignal(), BUY);
        assertEquals(result[21].getTime(), of(2018, 3, 18, 0, 0));
        assertEquals(result[21].getSignal(), SELL);
        assertEquals(result[27].getTime(), of(2018, 3, 24, 0, 0));
        assertEquals(result[27].getSignal(), BUY);
        assertEquals(result[47].getTime(), of(2018, 4, 13, 0, 0));
        assertEquals(result[47].getSignal(), SELL);
        assertEquals(result[50].getTime(), of(2018, 4, 16, 0, 0));
        assertEquals(result[50].getSignal(), BUY);
        assertEquals(result[54].getTime(), of(2018, 4, 20, 0, 0));
        assertEquals(result[54].getSignal(), SELL);
        assertEquals(result[56].getTime(), of(2018, 4, 22, 0, 0));
        assertEquals(result[56].getSignal(), BUY);
        assertEquals(result[69].getTime(), of(2018, 5, 5, 0, 0));
        assertEquals(result[69].getSignal(), BUY);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), SELL);
    }

    @Override
    protected EFIRequest buildIndicatorRequest() {
        return EFIRequest.builder()
                .originalData(originalData)
                .build();
    }

}
