package pro.crypto.indicator.dpo;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class DetrendedPriceOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testDetrendedPriceOscillatorWithPeriodSeven() {
        IndicatorResult[] expectedResult = loadExpectedResult("detrended_price_oscillator_1.json", DPOResult[].class);
        DPOResult[] actualResult = new DetrendedPriceOscillator(buildRequest(7, SIMPLE_MOVING_AVERAGE)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDetrendedPriceOscillatorWithPeriodTen() {
        IndicatorResult[] expectedResult = loadExpectedResult("detrended_price_oscillator_2.json", DPOResult[].class);
        DPOResult[] actualResult = new DetrendedPriceOscillator(buildRequest(10, EXPONENTIAL_MOVING_AVERAGE)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {DETRENDED_PRICE_OSCILLATOR}, size: {0}}");
        new DetrendedPriceOscillator(DPORequest.builder()
                .originalData(new Tick[0])
                .period(7)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {DETRENDED_PRICE_OSCILLATOR}}");
        new DetrendedPriceOscillator(DPORequest.builder()
                .originalData(null)
                .period(7)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {DETRENDED_PRICE_OSCILLATOR}, period: {7}, size: {6}}");
        new DetrendedPriceOscillator(DPORequest.builder()
                .originalData(new Tick[6])
                .period(7)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {DETRENDED_PRICE_OSCILLATOR}, period: {-7}}");
        new DetrendedPriceOscillator(DPORequest.builder()
                .originalData(new Tick[100])
                .period(-7)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {DETRENDED_PRICE_OSCILLATOR}}");
        new DetrendedPriceOscillator(DPORequest.builder()
                .originalData(new Tick[100])
                .period(7)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {DETRENDED_PRICE_OSCILLATOR}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new DetrendedPriceOscillator(DPORequest.builder()
                .originalData(new Tick[100])
                .period(7)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Override
    protected DPORequest buildRequest() {
        return DPORequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .build();
    }

    private DPORequest buildRequest(int period, IndicatorType movingAverageType) {
        DPORequest request = buildRequest();
        request.setPeriod(period);
        request.setMovingAverageType(movingAverageType);
        return request;
    }

}
