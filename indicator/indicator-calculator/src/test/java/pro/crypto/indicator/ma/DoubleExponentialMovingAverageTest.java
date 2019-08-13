package pro.crypto.indicator.ma;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.DOUBLE_EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class DoubleExponentialMovingAverageTest extends IndicatorAbstractTest {

    @Test
    public void testDoubleExponentialMovingAverageWithPeriodFourteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("double_exponential_moving_average.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {DOUBLE_EXPONENTIAL_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[0])
                .period(14)
                .indicatorType(DOUBLE_EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {DOUBLE_EXPONENTIAL_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(null)
                .period(14)
                .indicatorType(DOUBLE_EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {DOUBLE_EXPONENTIAL_MOVING_AVERAGE}, period: {14}, size: {13}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[13])
                .period(14)
                .indicatorType(DOUBLE_EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void doublePeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {DOUBLE_EXPONENTIAL_MOVING_AVERAGE}, period: {28}, size: {27}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[27])
                .period(14)
                .indicatorType(DOUBLE_EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {DOUBLE_EXPONENTIAL_MOVING_AVERAGE}, period: {-14}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .indicatorType(DOUBLE_EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {DOUBLE_EXPONENTIAL_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[100])
                .period(14)
                .indicatorType(DOUBLE_EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(14)
                .indicatorType(DOUBLE_EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

}
