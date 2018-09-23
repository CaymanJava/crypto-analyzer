package pro.crypto.indicator.rsi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.*;

public class RelativeStrengthIndexTest extends IndicatorAbstractTest {

    @Test
    public void testRelativeStrengthIndexWithSmoothedMovingAverage() {
        RSIRequest request = buildRequest();
        request.setMovingAverageType(SMOOTHED_MOVING_AVERAGE);
        RSIResult[] result = new RelativeStrengthIndex(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[5].getIndicatorValue());
        assertNull(result[12].getIndicatorValue());
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getIndicatorValue(), toBigDecimal(42.1006008344));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(37.4026074595));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(57.8833537021));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(68.1565666031));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(44.2698336999));
    }

    @Test
    public void testRelativeStrengthIndexWithExponentialMovingAverage() {
        RSIRequest request = buildRequest();
        request.setMovingAverageType(EXPONENTIAL_MOVING_AVERAGE);
        RSIResult[] result = new RelativeStrengthIndex(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[5].getIndicatorValue());
        assertNull(result[12].getIndicatorValue());
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getIndicatorValue(), toBigDecimal(42.1006008344));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(33.7850600453));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(64.4229260121));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(76.6942003843));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(34.7557515873));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {RELATIVE_STRENGTH_INDEX}, size: {0}}");
        new RelativeStrengthIndex(RSIRequest.builder()
                .originalData(new Tick[0])
                .movingAverageType(SMOOTHED_MOVING_AVERAGE)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {RELATIVE_STRENGTH_INDEX}}");
        new RelativeStrengthIndex(RSIRequest.builder()
                .originalData(null)
                .movingAverageType(SMOOTHED_MOVING_AVERAGE)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RELATIVE_STRENGTH_INDEX}, period: {20}, size: {19}}");
        new RelativeStrengthIndex(RSIRequest.builder()
                .originalData(new Tick[19])
                .movingAverageType(SMOOTHED_MOVING_AVERAGE)
                .period(20)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {RELATIVE_STRENGTH_INDEX}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new RelativeStrengthIndex(RSIRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .period(14)
                .build()).getResult();
    }

    @Override
    protected RSIRequest buildRequest() {
        return RSIRequest.builder()
                .originalData(originalData)
                .period(14)
                .build();
    }

}
