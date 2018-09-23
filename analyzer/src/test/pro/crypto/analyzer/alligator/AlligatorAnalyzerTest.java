package pro.crypto.analyzer.alligator;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.alligator.Alligator;
import pro.crypto.indicator.alligator.AlligatorRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.tick.TimeFrame.ONE_DAY;

public class AlligatorAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testAlligatorAnalyzer() {
        IndicatorResult[] indicatorResults = new Alligator(buildIndicatorRequest()).getResult();
        AlligatorAnalyzerResult[] result = new AlligatorAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getAwakePeriods(), 0);
        assertEquals(result[0].isTrend(), false);
        assertEquals(result[21].getTime(), of(2018, 3, 18, 0, 0));
        assertEquals(result[21].getAwakePeriods(), 1);
        assertEquals(result[21].isTrend(), true);
        assertEquals(result[27].getTime(), of(2018, 3, 24, 0, 0));
        assertEquals(result[27].getAwakePeriods(), 7);
        assertEquals(result[27].isTrend(), true);
        assertEquals(result[38].getTime(), of(2018, 4, 4, 0, 0));
        assertEquals(result[38].getAwakePeriods(), 1);
        assertEquals(result[38].isTrend(), true);
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getAwakePeriods(), 27);
        assertEquals(result[64].isTrend(), true);
        assertEquals(result[65].getTime(), of(2018, 5, 1, 0, 0));
        assertEquals(result[65].getAwakePeriods(), 28);
        assertEquals(result[65].isTrend(), false);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getAwakePeriods(), 1);
        assertEquals(result[72].isTrend(), false);
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return AlligatorRequest.builder()
                .originalData(originalData)
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build();
    }

}
