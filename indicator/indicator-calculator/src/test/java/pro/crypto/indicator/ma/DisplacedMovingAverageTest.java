package pro.crypto.indicator.ma;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.indicator.Shift;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.DISPLACED_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.HULL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.MOVING_AVERAGE_CONVERGENCE_DIVERGENCE;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.SMOOTHED_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.WEIGHTED_MOVING_AVERAGE;
import static pro.crypto.model.indicator.ShiftType.LEFT;
import static pro.crypto.model.indicator.ShiftType.RIGHT;
import static pro.crypto.model.tick.PriceType.CLOSE;
import static pro.crypto.model.tick.TimeFrame.FIFTEEN_MIN;
import static pro.crypto.model.tick.TimeFrame.ONE_DAY;

public class DisplacedMovingAverageTest extends IndicatorAbstractTest {

    @Test
    public void testDisplacedSimpleMovingAverage() {
        IndicatorResult[] expectedResult = loadExpectedResult("displaced_moving_average_1.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequest(SIMPLE_MOVING_AVERAGE)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDisplacedExponentialMovingAverage() {
        IndicatorResult[] expectedResult = loadExpectedResult("displaced_moving_average_2.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequest(EXPONENTIAL_MOVING_AVERAGE)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDisplacedHullMovingAverage() {
        IndicatorResult[] expectedResult = loadExpectedResult("displaced_moving_average_3.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequest(HULL_MOVING_AVERAGE)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDisplacedSmoothedMovingAverage() {
        IndicatorResult[] expectedResult = loadExpectedResult("displaced_moving_average_4.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequest(SMOOTHED_MOVING_AVERAGE)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDisplacedWeightedMovingAverage() {
        IndicatorResult[] expectedResult = loadExpectedResult("displaced_moving_average_5.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequest(WEIGHTED_MOVING_AVERAGE)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDisplacedSimpleMovingAverageWithLeftShift() {
        IndicatorResult[] expectedResult = loadExpectedResult("displaced_moving_average_6.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequestWithLeftShift()).getResult();
        assertArrayEquals(expectedResult, actualResult);
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

    private MARequest buildRequest(IndicatorType originalIndicatorType) {
        MARequest request = buildRequest();
        request.setOriginalIndicatorType(originalIndicatorType);
        return request;
    }

    private IndicatorRequest buildRequestWithLeftShift() {
        MARequest request = buildRequest(SIMPLE_MOVING_AVERAGE);
        request.getShift().setType(LEFT);
        return request;
    }

    @Override
    protected MARequest buildRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(20)
                .shift(new Shift(RIGHT, 3, ONE_DAY))
                .priceType(CLOSE)
                .indicatorType(DISPLACED_MOVING_AVERAGE)
                .build();
    }

}
