package pro.crypto.indicator.cfo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ChandeForecastOscillatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testChandeForecastOscillatorWithPeriodFive() {
        CFOResult[] result = new ChandeForecastOscillator(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[3].getIndicatorValue());
        assertEquals(result[4].getTime(), of(2018, 3, 1, 0, 0));
        assertEquals(result[4].getIndicatorValue(), toBigDecimal(3.7209067914));
        assertEquals(result[9].getTime(), of(2018, 3, 6, 0, 0));
        assertEquals(result[9].getIndicatorValue(), toBigDecimal(-4.2606671474));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-3.4471662332));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(5.4483663316));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(7.3420594365));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-1.5280234256));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CHANDE_FORECAST_OSCILLATOR}, size: {0}}");
        new ChandeForecastOscillator(CFORequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .period(5)
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
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CHANDE_FORECAST_OSCILLATOR}, period: {5}, size: {4}}");
        new ChandeForecastOscillator(CFORequest.builder()
                .originalData(new Tick[4])
                .priceType(CLOSE)
                .period(5)
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
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {CHANDE_FORECAST_OSCILLATOR}}");
        new ChandeForecastOscillator(CFORequest.builder()
                .originalData(new Tick[100])
                .period(5)
                .build()).getResult();
    }

    private CFORequest buildRequest() {
        return CFORequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .period(5)
                .build();
    }

}