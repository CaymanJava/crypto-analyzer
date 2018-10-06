package pro.crypto.indicator.di;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class DisparityIndexTest extends IndicatorAbstractTest {

    @Test
    public void testDisparityIndexWithPeriodFourteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("disparity_index_1.json", DIResult[].class);
        DIResult[] actualResult = new DisparityIndex(buildRequest(14)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDisparityIndexWithPeriodThirty() {
        IndicatorResult[] expectedResult = loadExpectedResult("disparity_index_2.json", DIResult[].class);
        DIResult[] actualResult = new DisparityIndex(buildRequest(30)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {DISPARITY_INDEX}, size: {0}}");
        new DisparityIndex(DIRequest.builder()
                .originalData(new Tick[0])
                .period(14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {DISPARITY_INDEX}}");
        new DisparityIndex(DIRequest.builder()
                .originalData(null)
                .period(14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {DISPARITY_INDEX}, period: {14}, size: {13}}");
        new DisparityIndex(DIRequest.builder()
                .originalData(new Tick[13])
                .period(14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {DISPARITY_INDEX}, period: {-14}}");
        new DisparityIndex(DIRequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {DISPARITY_INDEX}}");
        new DisparityIndex(DIRequest.builder()
                .originalData(new Tick[100])
                .period(14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {DISPARITY_INDEX}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new DisparityIndex(DIRequest.builder()
                .originalData(new Tick[100])
                .period(14)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Override
    protected DIRequest buildRequest() {
        return DIRequest.builder()
                .originalData(originalData)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

    private DIRequest buildRequest(int period) {
        DIRequest request = buildRequest();
        request.setPeriod(period);
        return request;
    }

}
