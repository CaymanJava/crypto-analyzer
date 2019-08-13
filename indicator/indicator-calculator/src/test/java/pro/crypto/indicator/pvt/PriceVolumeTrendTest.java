package pro.crypto.indicator.pvt;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class PriceVolumeTrendTest extends IndicatorAbstractTest {

    @Test
    public void testPriceVolumeTrend() {
        IndicatorResult[] expectedResult = loadExpectedResult("price_volume_trend.json", PVTResult[].class);
        PVTResult[] actualResult = new PriceVolumeTrend(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {PRICE_VOLUME_TREND}}");
        new PriceVolumeTrend(PVTRequest.builder()
                .originalData(new Tick[100])
                .movingAveragePeriod(14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {PRICE_VOLUME_TREND}}");
        new PriceVolumeTrend(PVTRequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .movingAveragePeriod(14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {PRICE_VOLUME_TREND}, size: {0}}");
        new PriceVolumeTrend(PVTRequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .movingAveragePeriod(14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void signalPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PRICE_VOLUME_TREND}, period: {-14}}");
        new PriceVolumeTrend(PVTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAveragePeriod(-14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {PRICE_VOLUME_TREND}}, " +
                "movingAverageType: {AVERAGE_TRUE_RANGE}");
        new PriceVolumeTrend(PVTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAveragePeriod(14)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .build()).getResult();
    }

    @Test
    public void notEnoughDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {PRICE_VOLUME_TREND}, period: {14}, size: {13}}");
        new PriceVolumeTrend(PVTRequest.builder()
                .originalData(new Tick[13])
                .priceType(CLOSE)
                .movingAveragePeriod(14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return PVTRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .movingAveragePeriod(9)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

}
