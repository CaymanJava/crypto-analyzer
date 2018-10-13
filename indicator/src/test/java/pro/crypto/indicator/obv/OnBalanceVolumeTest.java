package pro.crypto.indicator.obv;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;

public class OnBalanceVolumeTest extends IndicatorAbstractTest {

    @Test
    public void testOnBalanceVolume() {
        IndicatorResult[] expectedResult = loadExpectedResult("on_balance_volume.json", OBVResult[].class);
        OBVResult[] actualResult = new OnBalanceVolume(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ON_BALANCE_VOLUME}, size: {0}}");
        new OnBalanceVolume(OBVRequest.builder()
                .originalData(new Tick[0])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ON_BALANCE_VOLUME}}");
        new OnBalanceVolume(OBVRequest.builder()
                .originalData(null)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(14)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ON_BALANCE_VOLUME}, period: {14}, size: {13}}");
        new OnBalanceVolume(OBVRequest.builder()
                .originalData(new Tick[13])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(14)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ON_BALANCE_VOLUME}, period: {-14}");
        new OnBalanceVolume(OBVRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(-14)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {ON_BALANCE_VOLUME}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new OnBalanceVolume(OBVRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .movingAveragePeriod(14)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return OBVRequest.builder()
                .originalData(originalData)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(14)
                .build();
    }

}
