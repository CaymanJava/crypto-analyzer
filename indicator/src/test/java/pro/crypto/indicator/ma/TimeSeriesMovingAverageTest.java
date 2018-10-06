package pro.crypto.indicator.ma;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.TIME_SERIES_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;
import static pro.crypto.model.tick.PriceType.OPEN;

public class TimeSeriesMovingAverageTest extends IndicatorAbstractTest {

    @Test
    public void testTimeSeriesMovingAverageWithPeriodFifteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("time_series_moving_average_1.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequest(15)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testTimeSeriesMovingAverageWithPeriodTwenty() {
        IndicatorResult[] expectedResult = loadExpectedResult("time_series_moving_average_2.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequest(20)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {TIME_SERIES_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(TIME_SERIES_MOVING_AVERAGE)
                .originalData(new Tick[0])
                .period(5)
                .priceType(OPEN)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {TIME_SERIES_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(TIME_SERIES_MOVING_AVERAGE)
                .originalData(null)
                .period(5)
                .priceType(OPEN)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {TIME_SERIES_MOVING_AVERAGE}, period: {20}, size: {19}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(TIME_SERIES_MOVING_AVERAGE)
                .originalData(new Tick[19])
                .period(20)
                .priceType(OPEN)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {TIME_SERIES_MOVING_AVERAGE}, period: {-5}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(TIME_SERIES_MOVING_AVERAGE)
                .originalData(new Tick[100])
                .period(-5)
                .priceType(OPEN)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {TIME_SERIES_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(TIME_SERIES_MOVING_AVERAGE)
                .originalData(new Tick[100])
                .period(5)
                .build()).getResult();
    }

    @Override
    protected MARequest buildRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .indicatorType(TIME_SERIES_MOVING_AVERAGE)
                .build();
    }

    private MARequest buildRequest(int period) {
        MARequest request = buildRequest();
        request.setPeriod(period);
        return request;
    }

}
