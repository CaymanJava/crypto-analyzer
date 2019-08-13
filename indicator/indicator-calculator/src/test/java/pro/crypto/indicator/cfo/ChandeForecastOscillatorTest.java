package pro.crypto.indicator.cfo;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.KELTNER_CHANNEL;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ChandeForecastOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testChandeForecastOscillatorWithPeriodFive() {
        IndicatorResult[] expectedResult = loadExpectedResult("chande_forecast_oscillator.json", CFOResult[].class);
        CFOResult[] actualResult = new ChandeForecastOscillator(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CHANDE_FORECAST_OSCILLATOR}, size: {0}}");
        new ChandeForecastOscillator(CFORequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .period(5)
                .movingAveragePeriod(10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CHANDE_FORECAST_OSCILLATOR}}");
        new ChandeForecastOscillator(CFORequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .period(5)
                .movingAveragePeriod(10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CHANDE_FORECAST_OSCILLATOR}, period: {15}, size: {14}}");
        new ChandeForecastOscillator(CFORequest.builder()
                .originalData(new Tick[14])
                .priceType(CLOSE)
                .period(5)
                .movingAveragePeriod(10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHANDE_FORECAST_OSCILLATOR}, period: {-5}}");
        new ChandeForecastOscillator(CFORequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(-5)
                .movingAveragePeriod(10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void movingAveragePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHANDE_FORECAST_OSCILLATOR}, period: {-10}}");
        new ChandeForecastOscillator(CFORequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(5)
                .movingAveragePeriod(-10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {CHANDE_FORECAST_OSCILLATOR}}");
        new ChandeForecastOscillator(CFORequest.builder()
                .originalData(new Tick[100])
                .period(5)
                .movingAveragePeriod(10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {CHANDE_FORECAST_OSCILLATOR}}," +
                " movingAverageType: {KELTNER_CHANNEL}");
        new ChandeForecastOscillator(CFORequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(5)
                .movingAveragePeriod(10)
                .movingAverageType(KELTNER_CHANNEL)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return CFORequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .period(5)
                .movingAveragePeriod(10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

}
