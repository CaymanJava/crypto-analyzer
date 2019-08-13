package pro.crypto.indicator.atrb;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class AverageTrueRangeBandsTest extends IndicatorAbstractTest {

    @Test
    public void testAverageTrueRangeBandsWithShiftThree() {
        IndicatorResult[] expectedResult = loadExpectedResult("average_true_range_bands_1.json", ATRBResult[].class);
        ATRBResult[] actualResult = new AverageTrueRangeBands(buildShiftThreeRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testAverageTrueRangeBandsWithShiftOne() {
        IndicatorResult[] expectedResult = loadExpectedResult("average_true_range_bands_2.json", ATRBResult[].class);
        ATRBResult[] actualResult = new AverageTrueRangeBands(buildShiftOneRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {AVERAGE_TRUE_RANGE_BANDS}, size: {0}}");
        new AverageTrueRangeBands(ATRBRequest.builder()
                .originalData(new Tick[0])
                .period(5)
                .shift(3)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {AVERAGE_TRUE_RANGE_BANDS}}");
        new AverageTrueRangeBands(ATRBRequest.builder()
                .originalData(null)
                .period(5)
                .shift(3)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {AVERAGE_TRUE_RANGE_BANDS}, period: {5}, size: {4}}");
        new AverageTrueRangeBands(ATRBRequest.builder()
                .originalData(new Tick[4])
                .period(5)
                .shift(3)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {AVERAGE_TRUE_RANGE_BANDS}, period: {-5}}");
        new AverageTrueRangeBands(ATRBRequest.builder()
                .originalData(new Tick[100])
                .period(-5)
                .shift(3)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void shiftLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Shift should be more or equals 0 {indicator: {AVERAGE_TRUE_RANGE_BANDS}, shift: {-3.00}}");
        new AverageTrueRangeBands(ATRBRequest.builder()
                .originalData(new Tick[100])
                .period(5)
                .shift(-3.0)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {AVERAGE_TRUE_RANGE_BANDS}}");
        new AverageTrueRangeBands(ATRBRequest.builder()
                .originalData(new Tick[100])
                .period(5)
                .shift(3)
                .build()).getResult();
    }

    @Override
    protected ATRBRequest buildRequest() {
        return ATRBRequest.builder()
                .originalData(originalData)
                .period(5)
                .priceType(CLOSE)
                .build();
    }

    private ATRBRequest buildShiftThreeRequest() {
        ATRBRequest request = buildRequest();
        request.setShift(3);
        return request;
    }

    private ATRBRequest buildShiftOneRequest() {
        ATRBRequest request = buildRequest();
        request.setShift(1);
        return request;
    }

}
