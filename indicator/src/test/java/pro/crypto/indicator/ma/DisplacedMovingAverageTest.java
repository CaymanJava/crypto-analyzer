package pro.crypto.indicator.ma;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.Shift;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.*;
import static pro.crypto.model.ShiftType.RIGHT;
import static pro.crypto.model.tick.PriceType.CLOSE;
import static pro.crypto.model.tick.TimeFrame.FIFTEEN_MIN;
import static pro.crypto.model.tick.TimeFrame.ONE_DAY;

public class DisplacedMovingAverageTest extends IndicatorAbstractTest {

    private final static int SHIFT = 3;

    @Test
    public void displaceSimpleMovingAverageTest() {
        MARequest request = buildRequest();
        request.setOriginalIndicatorType(SIMPLE_MOVING_AVERAGE);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length + SHIFT);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[21].getIndicatorValue());
        assertEquals(result[22].getTime(), of(2018, 3, 19, 0, 0));
        assertEquals(result[22].getIndicatorValue(), toBigDecimal(1251.06901));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(1192.538015));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1234.68751));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(1353.772505));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(1419.4345));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1425.282995));
        assertEquals(result[75].getTime(), of(2018, 5, 11, 0, 0));
        assertEquals(result[75].getIndicatorValue(), toBigDecimal(1427.10249));
    }

    @Test
    public void displaceExponentialMovingAverageTest() {
        MARequest request = buildRequest();
        request.setOriginalIndicatorType(EXPONENTIAL_MOVING_AVERAGE);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length + SHIFT);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[21].getIndicatorValue());
        assertEquals(result[22].getTime(), of(2018, 3, 19, 0, 0));
        assertEquals(result[22].getIndicatorValue(), toBigDecimal(1251.06901));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(1196.4726886844));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1266.473236253));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(1350.1020834487));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(1421.1341679168));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1412.9619751106));
        assertEquals(result[75].getTime(), of(2018, 5, 11, 0, 0));
        assertEquals(result[75].getIndicatorValue(), toBigDecimal(1405.7344873457));
    }

    @Test
    public void displaceHullMovingAverageTest() {
        MARequest request = buildRequest();
        request.setOriginalIndicatorType(HULL_MOVING_AVERAGE);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length + SHIFT);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[21].getIndicatorValue());
        assertEquals(result[22].getTime(), of(2018, 3, 19, 0, 0));
        assertEquals(result[22].getIndicatorValue(), toBigDecimal(1175.29701));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(1130.484045));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1357.60251));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(1406.729495));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(1503.1315));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1447.986965));
        assertEquals(result[75].getTime(), of(2018, 5, 11, 0, 0));
        assertEquals(result[75].getIndicatorValue(), toBigDecimal(1395.69149));
    }

    @Test
    public void displaceSmoothedMovingAverageTest() {
        MARequest request = buildRequest();
        request.setOriginalIndicatorType(SMOOTHED_MOVING_AVERAGE);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length + SHIFT);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[21].getIndicatorValue());
        assertEquals(result[22].getTime(), of(2018, 3, 19, 0, 0));
        assertEquals(result[22].getIndicatorValue(), toBigDecimal(1251.06901));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(1217.1978417736));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1252.0261740035));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(1315.0864673457));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(1375.5470383595));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1379.5354994627));
        assertEquals(result[75].getTime(), of(2018, 5, 11, 0, 0));
        assertEquals(result[75].getIndicatorValue(), toBigDecimal(1380.4094976018));
    }

    @Test
    public void displaceWeightedMovingAverageTest() {
        MARequest request = buildRequest();
        request.setOriginalIndicatorType(WEIGHTED_MOVING_AVERAGE);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length + SHIFT);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[21].getIndicatorValue());
        assertEquals(result[22].getTime(), of(2018, 3, 19, 0, 0));
        assertEquals(result[22].getIndicatorValue(), toBigDecimal(1231.0905838095));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(1173.5982580952));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1270.7128628571));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(1369.7909052381));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(1440.1948057143));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1431.5910352381));
        assertEquals(result[75].getTime(), of(2018, 5, 11, 0, 0));
        assertEquals(result[75].getIndicatorValue(), toBigDecimal(1419.8086090476));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {DISPLACED_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[0])
                .period(5)
                .indicatorType(DISPLACED_MOVING_AVERAGE)
                .originalIndicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .shift(new Shift(RIGHT, 3, FIFTEEN_MIN))
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {DISPLACED_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(null)
                .period(5)
                .indicatorType(DISPLACED_MOVING_AVERAGE)
                .originalIndicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .shift(new Shift(RIGHT, 3, FIFTEEN_MIN))
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {DISPLACED_MOVING_AVERAGE}, period: {5}, size: {1}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[1])
                .period(5)
                .indicatorType(DISPLACED_MOVING_AVERAGE)
                .originalIndicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .shift(new Shift(RIGHT, 3, FIFTEEN_MIN))
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {DISPLACED_MOVING_AVERAGE}, period: {-5}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[1])
                .period(-5)
                .indicatorType(DISPLACED_MOVING_AVERAGE)
                .originalIndicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .shift(new Shift(RIGHT, 3, FIFTEEN_MIN))
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {DISPLACED_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[30])
                .period(5)
                .indicatorType(DISPLACED_MOVING_AVERAGE)
                .originalIndicatorType(HULL_MOVING_AVERAGE)
                .priceType(null)
                .shift(new Shift(RIGHT, 3, FIFTEEN_MIN))
                .build()).getResult();
    }

    @Test
    public void wrongOriginalIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {DISPLACED_MOVING_AVERAGE}}," +
                " movingAverageType: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[30])
                .period(5)
                .indicatorType(DISPLACED_MOVING_AVERAGE)
                .originalIndicatorType(MOVING_AVERAGE_CONVERGENCE_DIVERGENCE)
                .priceType(CLOSE)
                .shift(new Shift(RIGHT, 3, FIFTEEN_MIN))
                .build()).getResult();
    }

    @Override
    protected MARequest buildRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(20)
                .shift(new Shift(RIGHT, SHIFT, ONE_DAY))
                .priceType(CLOSE)
                .indicatorType(DISPLACED_MOVING_AVERAGE)
                .build();
    }

}
