package pro.crypto.indicator.ma;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;
import static pro.crypto.model.tick.PriceType.OPEN;

public class ExponentialMovingAverageTest extends IndicatorAbstractTest {

    @Test
    public void testExponentialMovingAverageWithPeriodFifteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("exponential_moving_average_1.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequest(15)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testExponentialMovingAverageWithPeriodTwenty() {
        IndicatorResult[] expectedResult = loadExpectedResult("exponential_moving_average_2.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequest(20)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testExponentialMovingAverageWithAlphaCoefficient() {
        IndicatorResult[] expectedResult = loadExpectedResult("exponential_moving_average_3.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequestWithAlphaCoefficient()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {EXPONENTIAL_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(new Tick[0])
                .period(5)
                .priceType(OPEN)
                .alphaCoefficient(toBigDecimal(0.4))
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {EXPONENTIAL_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(null)
                .period(5)
                .priceType(OPEN)
                .alphaCoefficient(toBigDecimal(0.4))
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {EXPONENTIAL_MOVING_AVERAGE}, period: {5}, size: {1}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(new Tick[1])
                .period(5)
                .priceType(OPEN)
                .alphaCoefficient(toBigDecimal(0.4))
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {EXPONENTIAL_MOVING_AVERAGE}, period: {-5}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(new Tick[1])
                .period(-5)
                .priceType(OPEN)
                .alphaCoefficient(toBigDecimal(0.4))
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {EXPONENTIAL_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(new Tick[30])
                .period(5)
                .priceType(null)
                .alphaCoefficient(toBigDecimal(0.4))
                .build()).getResult();
    }

    @Override
    protected MARequest buildRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

    private MARequest buildRequestWithAlphaCoefficient() {
        MARequest request = buildRequest(15);
        request.setAlphaCoefficient(toBigDecimal(0.4));
        return request;
    }

    private MARequest buildRequest(int period) {
        MARequest request = buildRequest();
        request.setPeriod(period);
        return request;
    }

}
