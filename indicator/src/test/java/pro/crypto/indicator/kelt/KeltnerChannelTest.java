package pro.crypto.indicator.kelt;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class KeltnerChannelTest extends IndicatorAbstractTest {

    @Test
    public void testKeltnerChannelWithDefaultParameters() {
        IndicatorResult[] expectedResult = loadExpectedResult("keltner_channel.json", KELTResult[].class);
        KELTResult[] actualResult = new KeltnerChannel(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {KELTNER_CHANNEL}, size: {0}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {KELTNER_CHANNEL}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void movingAveragePeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {KELTNER_CHANNEL}, period: {20}, size: {19}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[19])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void averageTrueRangePeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {KELTNER_CHANNEL}, period: {20}, size: {19}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[19])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(10)
                .averageTrueRangePeriod(20)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void averageTrueRangeShiftLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Shift should be more or equals 0 {indicator: {KELTNER_CHANNEL}, shift: {-2.00}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(-2)
                .build()).getResult();
    }

    @Test
    public void movingAveragePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KELTNER_CHANNEL}, period: {-20}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(-20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void averageTrueRangePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KELTNER_CHANNEL}, period: {-10}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(-10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {KELTNER_CHANNEL}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {KELTNER_CHANNEL}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return KELTRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build();
    }

}
