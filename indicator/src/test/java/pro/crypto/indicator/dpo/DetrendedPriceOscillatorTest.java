package pro.crypto.indicator.dpo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class DetrendedPriceOscillatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testDetrendedPriceOscillatorWithPeriodSeven() {
        DPOResult[] result = new DetrendedPriceOscillator(buildRequest(7, SIMPLE_MOVING_AVERAGE)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[9].getIndicatorValue());
        assertEquals(result[10].getTime(), of(2018, 3, 7, 0, 0));
        assertEquals(result[10].getIndicatorValue(), toBigDecimal(-31.8586857143));
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertEquals(result[20].getIndicatorValue(), toBigDecimal(-6.1371428571));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(123.4299857143));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(88.8828428571));
        assertEquals(result[66].getTime(), of(2018, 5, 2, 0, 0));
        assertEquals(result[66].getIndicatorValue(), toBigDecimal(-53.3557142857));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-72.1328428571));
    }

    @Test
    public void testDetrendedPriceOscillatorWithPeriodThree() {
        DPOResult[] result = new DetrendedPriceOscillator(buildRequest(10, EXPONENTIAL_MOVING_AVERAGE)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[14].getIndicatorValue());
        assertEquals(result[15].getTime(), of(2018, 3, 12, 0, 0));
        assertEquals(result[15].getIndicatorValue(), toBigDecimal(-106.78501));
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertEquals(result[20].getIndicatorValue(), toBigDecimal(-31.3697355621));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(113.5218679773));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(112.9968445102));
        assertEquals(result[54].getTime(), of(2018, 4, 20, 0, 0));
        assertEquals(result[54].getIndicatorValue(), toBigDecimal(34.146461965));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-78.9917860818));
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

    private IndicatorRequest buildRequest(int period, IndicatorType indicatorType) {
        return DPORequest.builder()
                .originalData(originalData)
                .period(period)
                .movingAverageType(indicatorType)
                .priceType(CLOSE)
                .build();
    }

}