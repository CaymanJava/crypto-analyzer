package pro.crypto.indicator.ma;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.FifteenMinTickWithClosePriceOnlyGenerator;
import pro.crypto.model.Shift;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.*;
import static pro.crypto.model.ShiftType.RIGHT;
import static pro.crypto.model.tick.PriceType.CLOSE;
import static pro.crypto.model.tick.TimeFrame.FIFTEEN_MIN;

public class DisplacedMovingAverageTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    private MARequest request;

    @Before
    public void init() {
        FifteenMinTickWithClosePriceOnlyGenerator generator = new FifteenMinTickWithClosePriceOnlyGenerator(of(2018, 2, 25, 0, 0));
        originalData = generator.generate();
        request = buildMovingAverageCreationRequest();
    }

    @Test
    public void displaceSimpleMovingAverageTest() {
        request.setOriginalIndicatorType(SIMPLE_MOVING_AVERAGE);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length + request.getShift().getValue());
        assertNull(result[0].getIndicatorValue());
        assertNull(result[4].getIndicatorValue());
        assertEquals(result[5].getTime(), of(2018, 2, 25, 1, 15));
        assertEquals(result[5].getIndicatorValue(), toBigDecimal(6.5));
        assertEquals(result[19].getTime(), of(2018, 2, 25, 4, 45));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(7.1666666667));
    }

    @Test
    public void displaceExponentialMovingAverageTest() {
        request.setOriginalIndicatorType(EXPONENTIAL_MOVING_AVERAGE);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length + request.getShift().getValue());
        assertNull(result[0].getIndicatorValue());
        assertNull(result[4].getIndicatorValue());
        assertEquals(result[5].getTime(), of(2018, 2, 25, 1, 15));
        assertEquals(result[5].getIndicatorValue(), toBigDecimal(6.5));
        assertEquals(result[19].getTime(), of(2018, 2, 25, 4, 45));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(7.0685180665));
    }

    @Test
    public void displaceHullMovingAverageTest() {
        request.setOriginalIndicatorType(HULL_MOVING_AVERAGE);
        request.setPeriod(4);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length + request.getShift().getValue());
        assertNull(result[0].getIndicatorValue());
        assertNull(result[5].getIndicatorValue());
        assertEquals(result[6].getTime(), of(2018, 2, 25, 1, 30));
        assertEquals(result[6].getIndicatorValue(), toBigDecimal(7.65));
        assertEquals(result[19].getTime(), of(2018, 2, 25, 4, 45));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(7.875));
        request.setPeriod(3);
    }

    @Test
    public void displaceSmoothedMovingAverageTest() {
        request.setOriginalIndicatorType(SMOOTHED_MOVING_AVERAGE);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length + request.getShift().getValue());
        assertNull(result[0].getIndicatorValue());
        assertNull(result[4].getIndicatorValue());
        assertEquals(result[5].getTime(), of(2018, 2, 25, 1, 15));
        assertEquals(result[5].getIndicatorValue(), toBigDecimal(6.5));
        assertEquals(result[19].getTime(), of(2018, 2, 25, 4, 45));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(6.8478116417));
    }

    @Test
    public void displaceWeightedMovingAverageTest() {
        request.setOriginalIndicatorType(WEIGHTED_MOVING_AVERAGE);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length + request.getShift().getValue());
        assertNull(result[0].getIndicatorValue());
        assertNull(result[4].getIndicatorValue());
        assertEquals(result[5].getTime(), of(2018, 2, 25, 1, 15));
        assertEquals(result[5].getIndicatorValue(), toBigDecimal(6.6166666667));
        assertEquals(result[19].getTime(), of(2018, 2, 25, 4, 45));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(7.2333333333));
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

    private MARequest buildMovingAverageCreationRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(3)
                .shift(new Shift(RIGHT, 3, FIFTEEN_MIN))
                .priceType(CLOSE)
                .indicatorType(DISPLACED_MOVING_AVERAGE)
                .build();
    }

}