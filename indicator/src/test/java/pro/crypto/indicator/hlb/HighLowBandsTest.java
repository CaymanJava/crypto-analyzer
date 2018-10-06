package pro.crypto.indicator.hlb;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class HighLowBandsTest extends IndicatorAbstractTest {

    @Test
    public void testHighLowBandsWithPeriodThirteenAndFivePercentageShift() {
        IndicatorResult[] expectedResult = loadExpectedResult("high_low_bands_1.json", HLBResult[].class);
        HLBResult[] actualResult = new HighLowBands(buildRequest(13, 5)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testHighLowBandsWithPeriodFourteenAndFourPercentageShift() {
        IndicatorResult[] expectedResult = loadExpectedResult("high_low_bands_2.json", HLBResult[].class);
        HLBResult[] actualResult = new HighLowBands(buildRequest(14, 4)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {HIGH_LOW_BANDS}, size: {0}}");
        new HighLowBands(HLBRequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .period(14)
                .shiftPercentage(4)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {HIGH_LOW_BANDS}}");
        new HighLowBands(HLBRequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .period(14)
                .shiftPercentage(4)
                .build()).getResult();
    }

    @Test
    public void shiftPercentageLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Shift should be more or equals 0 {indicator: {HIGH_LOW_BANDS}, shift: {-4.00}}");
        new HighLowBands(HLBRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(14)
                .shiftPercentage(-4)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {HIGH_LOW_BANDS}, period: {14}, size: {13}}");
        new HighLowBands(HLBRequest.builder()
                .originalData(new Tick[13])
                .priceType(CLOSE)
                .period(14)
                .shiftPercentage(4)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {HIGH_LOW_BANDS}, period: {-14}}");
        new HighLowBands(HLBRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(-14)
                .shiftPercentage(4)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {HIGH_LOW_BANDS}}");
        new HighLowBands(HLBRequest.builder()
                .originalData(new Tick[100])
                .period(14)
                .shiftPercentage(4)
                .build()).getResult();
    }

    @Override
    protected HLBRequest buildRequest() {
        return HLBRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .build();
    }

    private HLBRequest buildRequest(int period, int shift) {
        HLBRequest request = buildRequest();
        request.setPeriod(period);
        request.setShiftPercentage(shift);
        return request;
    }

}
