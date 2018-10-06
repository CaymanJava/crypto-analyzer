package pro.crypto.indicator.vi;

import org.junit.Test;
import pro.crypto.exception.UnknownTypeException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class VolumeIndexTest extends IndicatorAbstractTest {

    @Test
    public void testNegativeVolumeIndexWithPeriodTwentyFive() {
        IndicatorResult[] expectedResult = loadExpectedResult("volume_index_1.json", VIResult[].class);
        VIResult[] actualResult = VolumeIndexFactory.create(buildRequest(NEGATIVE_VOLUME_INDEX)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testPositiveVolumeIndexWithPeriodTwentyFive() {
        IndicatorResult[] expectedResult = loadExpectedResult("volume_index_2.json", VIResult[].class);
        VIResult[] actualResult = VolumeIndexFactory.create(buildRequest(POSITIVE_VOLUME_INDEX)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {NEGATIVE_VOLUME_INDEX}, size: {0}}");
        VolumeIndexFactory.create(VIRequest.builder()
                .originalData(new Tick[0])
                .period(25)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .volumeIndexType(NEGATIVE_VOLUME_INDEX)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {NEGATIVE_VOLUME_INDEX}}");
        VolumeIndexFactory.create(VIRequest.builder()
                .originalData(null)
                .period(25)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .volumeIndexType(NEGATIVE_VOLUME_INDEX)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {NEGATIVE_VOLUME_INDEX}, period: {25}, size: {24}}");
        VolumeIndexFactory.create(VIRequest.builder()
                .originalData(new Tick[24])
                .period(25)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .volumeIndexType(NEGATIVE_VOLUME_INDEX)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {NEGATIVE_VOLUME_INDEX}, period: {-25}}");
        VolumeIndexFactory.create(VIRequest.builder()
                .originalData(new Tick[100])
                .period(-25)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .volumeIndexType(NEGATIVE_VOLUME_INDEX)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {NEGATIVE_VOLUME_INDEX}}");
        VolumeIndexFactory.create(VIRequest.builder()
                .originalData(new Tick[100])
                .period(25)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .volumeIndexType(NEGATIVE_VOLUME_INDEX)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {NEGATIVE_VOLUME_INDEX}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        VolumeIndexFactory.create(VIRequest.builder()
                .originalData(new Tick[100])
                .period(25)
                .priceType(CLOSE)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .volumeIndexType(NEGATIVE_VOLUME_INDEX)
                .build()).getResult();
    }

    @Test
    public void wrongVolumeIndexTypeTest() {
        expectedException.expect(UnknownTypeException.class);
        expectedException.expectMessage("Unknown volume index type {type: {AVERAGE_DIRECTIONAL_MOVEMENT_INDEX}}");
        VolumeIndexFactory.create(VIRequest.builder()
                .originalData(new Tick[100])
                .period(25)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .volumeIndexType(AVERAGE_DIRECTIONAL_MOVEMENT_INDEX)
                .build()).getResult();
    }

    @Override
    protected VIRequest buildRequest() {
        return VIRequest.builder()
                .originalData(originalData)
                .period(25)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

    private IndicatorRequest buildRequest(IndicatorType volumeIndexType) {
        VIRequest request = buildRequest();
        request.setVolumeIndexType(volumeIndexType);
        return request;
    }

}
