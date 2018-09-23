package pro.crypto.indicator.cfo;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.KELTNER_CHANNEL;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ChandeForecastOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testChandeForecastOscillatorWithPeriodFive() {
        CFOResult[] result = new ChandeForecastOscillator(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineValue());
        assertNull(result[3].getIndicatorValue());
        assertNull(result[3].getSignalLineValue());
        assertEquals(result[4].getTime(), of(2018, 3, 1, 0, 0));
        assertEquals(result[4].getIndicatorValue(), toBigDecimal(3.7209067914));
        assertNull(result[4].getSignalLineValue());
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getIndicatorValue(), toBigDecimal(-5.7267646119));
        assertEquals(result[13].getSignalLineValue(), toBigDecimal(-1.7635916039));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-3.4471662332));
        assertEquals(result[19].getSignalLineValue(), toBigDecimal(-1.6988302657));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(5.4483663316));
        assertEquals(result[32].getSignalLineValue(), toBigDecimal(4.6619455402));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(7.3420594365));
        assertEquals(result[45].getSignalLineValue(), toBigDecimal(4.3493664957));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-1.5280234256));
        assertEquals(result[72].getSignalLineValue(), toBigDecimal(-1.9645105579));
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
