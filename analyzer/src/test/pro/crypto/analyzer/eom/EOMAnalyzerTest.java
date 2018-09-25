package pro.crypto.analyzer.eom;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.eom.EOMRequest;
import pro.crypto.indicator.eom.EaseOfMovement;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.Signal.*;

public class EOMAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testEaseOfMovementAnalyzerWithPeriodFourteen() {
        IndicatorResult[] indicatorResults = new EaseOfMovement(buildIndicatorRequest()).getResult();
        EOMAnalyzerResult[] result = new EOMAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getSignal(), BUY);
        assertEquals(result[66].getTime(), of(2018, 5, 2, 0, 0));
        assertEquals(result[66].getSignal(), SELL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), NEUTRAL);
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return EOMRequest.builder()
                .originalData(originalData)
                .movingAveragePeriod(14)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build();
    }

}
