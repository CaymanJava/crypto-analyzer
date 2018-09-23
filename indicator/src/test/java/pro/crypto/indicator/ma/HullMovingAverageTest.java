package pro.crypto.indicator.ma;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.HULL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class HullMovingAverageTest extends IndicatorAbstractTest {

    @Test
    public void testHullMovingAverageWithPeriodFifteen() {
        MARequest request = buildRequest();
        request.setPeriod(15);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[13].getIndicatorValue());
        assertEquals(result[14].getTime(), of(2018, 3, 11, 0, 0));
        assertEquals(result[14].getIndicatorValue(), toBigDecimal(1217.9616123809));
        assertEquals(result[24].getTime(), of(2018, 3, 21, 0, 0));
        assertEquals(result[24].getIndicatorValue(), toBigDecimal(1144.8501609525));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(1201.6914819047));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1347.9031714286));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(1423.0791361905));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(1430.7283657142));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1353.9599847619));
    }

    @Test
    public void testHullMovingAverageWithPeriodTwenty() {
        MARequest request = buildRequest();
        request.setPeriod(20);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[18].getIndicatorValue());
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(1175.29701));
        assertEquals(result[24].getTime(), of(2018, 3, 21, 0, 0));
        assertEquals(result[24].getIndicatorValue(), toBigDecimal(1142.625045));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(1165.571985));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1376.30552));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(1402.15751));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(1469.798465));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1395.69149));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {HULL_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[0])
                .period(5)
                .indicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {HULL_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(null)
                .period(5)
                .indicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {HULL_MOVING_AVERAGE}, period: {5}, size: {1}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[1])
                .period(5)
                .indicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {HULL_MOVING_AVERAGE}, period: {-5}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[1])
                .period(-5)
                .indicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {HULL_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[30])
                .period(5)
                .indicatorType(HULL_MOVING_AVERAGE)
                .priceType(null)
                .build()).getResult();
    }

    @Override
    protected MARequest buildRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .indicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

}
