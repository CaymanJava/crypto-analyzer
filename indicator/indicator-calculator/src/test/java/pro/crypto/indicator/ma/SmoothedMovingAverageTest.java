package pro.crypto.indicator.ma;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.SMOOTHED_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class SmoothedMovingAverageTest extends IndicatorAbstractTest {

    @Test
    public void testSmoothedMovingAverageWithPeriodFifteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("smoothed_moving_average_1.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequest(15)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testSmoothedMovingAverageWithPeriodTwenty() {
        IndicatorResult[] expectedResult = loadExpectedResult("smoothed_moving_average_2.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequest(20)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {SMOOTHED_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[0])
                .period(5)
                .indicatorType(SMOOTHED_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {SMOOTHED_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(null)
                .period(5)
                .indicatorType(SMOOTHED_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {SMOOTHED_MOVING_AVERAGE}, period: {5}, size: {1}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[1])
                .period(5)
                .indicatorType(SMOOTHED_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {SMOOTHED_MOVING_AVERAGE}, period: {-5}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[1])
                .period(-5)
                .indicatorType(SMOOTHED_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {SMOOTHED_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[30])
                .period(5)
                .indicatorType(SMOOTHED_MOVING_AVERAGE)
                .priceType(null)
                .build()).getResult();
    }

    @Override
    protected MARequest buildRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .indicatorType(SMOOTHED_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

    private MARequest buildRequest(int period) {
        MARequest request = buildRequest();
        request.setPeriod(period);
        return request;
    }

}
