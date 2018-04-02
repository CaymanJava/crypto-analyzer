package pro.crypto.indicators.ma;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.FifteenMinTickWithClosePriceOnlyGenerator;
import pro.crypto.model.Shift;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.Tick;
import pro.crypto.model.request.MARequest;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.*;
import static pro.crypto.model.tick.PriceType.CLOSE;
import static pro.crypto.model.ShiftType.RIGHT;
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
        MAResult[] result = MovingAverageFactory.createMovingAverage(request).getResult();
        assertTrue(result.length == originalData.length + request.getShift().getValue());
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[4].getIndicatorValue()));
        assertEquals(result[5].getTime(), of(2018, 2, 25, 1, 15));
        assertEquals(result[5].getIndicatorValue(), new BigDecimal(6.5000000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[19].getTime(), of(2018, 2, 25, 4, 45));
        assertEquals(result[19].getIndicatorValue(), new BigDecimal(7.1666666667).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void displaceExponentialMovingAverageTest() {
        request.setOriginalIndicatorType(EXPONENTIAL_MOVING_AVERAGE);
        MAResult[] result = MovingAverageFactory.createMovingAverage(request).getResult();
        assertTrue(result.length == originalData.length + request.getShift().getValue());
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[4].getIndicatorValue()));
        assertEquals(result[5].getTime(), of(2018, 2, 25, 1, 15));
        assertEquals(result[5].getIndicatorValue(), new BigDecimal(6.5000000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[19].getTime(), of(2018, 2, 25, 4, 45));
        assertEquals(result[19].getIndicatorValue(), new BigDecimal(7.0685180665).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void displaceHullMovingAverageTest() {
        request.setOriginalIndicatorType(HULL_MOVING_AVERAGE);
        request.setPeriod(4);
        MAResult[] result = MovingAverageFactory.createMovingAverage(request).getResult();
        assertTrue(result.length == originalData.length + request.getShift().getValue());
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[5].getIndicatorValue()));
        assertEquals(result[6].getTime(), of(2018, 2, 25, 1, 30));
        assertEquals(result[6].getIndicatorValue(), new BigDecimal(7.6500000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[19].getTime(), of(2018, 2, 25, 4, 45));
        assertEquals(result[19].getIndicatorValue(), new BigDecimal(7.8750000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        request.setPeriod(3);
    }

    @Test
    public void displaceSmoothedMovingAverageTest() {
        request.setOriginalIndicatorType(SMOOTHED_MOVING_AVERAGE);
        MAResult[] result = MovingAverageFactory.createMovingAverage(request).getResult();
        assertTrue(result.length == originalData.length + request.getShift().getValue());
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[4].getIndicatorValue()));
        assertEquals(result[5].getTime(), of(2018, 2, 25, 1, 15));
        assertEquals(result[5].getIndicatorValue(), new BigDecimal(6.5000000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[19].getTime(), of(2018, 2, 25, 4, 45));
        assertEquals(result[19].getIndicatorValue(), new BigDecimal(6.8478116417).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void displaceWeightedMovingAverageTest() {
        request.setOriginalIndicatorType(WEIGHTED_MOVING_AVERAGE);
        MAResult[] result = MovingAverageFactory.createMovingAverage(request).getResult();
        assertTrue(result.length == originalData.length + request.getShift().getValue());
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[4].getIndicatorValue()));
        assertEquals(result[5].getTime(), of(2018, 2, 25, 1, 15));
        assertEquals(result[5].getIndicatorValue(), new BigDecimal(6.6166666667).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[19].getTime(), of(2018, 2, 25, 4, 45));
        assertEquals(result[19].getIndicatorValue(), new BigDecimal(7.2333333333).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {DISPLACED_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.createMovingAverage(MARequest.builder()
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
        MovingAverageFactory.createMovingAverage(MARequest.builder()
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
        MovingAverageFactory.createMovingAverage(MARequest.builder()
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
        MovingAverageFactory.createMovingAverage(MARequest.builder()
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
        MovingAverageFactory.createMovingAverage(MARequest.builder()
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
        MovingAverageFactory.createMovingAverage(MARequest.builder()
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