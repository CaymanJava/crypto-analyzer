package pro.crypto.indicator.ro;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RainbowOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testRainbowOscillator() {
        ROResult[] result = new RainbowOscillator(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[9].getIndicatorValue());
        assertEquals(result[10].getTime(), of(2018, 3, 7, 0, 0));
        assertEquals(result[10].getIndicatorValue(), toBigDecimal(-43.5067318925));
        assertEquals(result[10].getUpperEnvelope(), toBigDecimal(52.0240684705));
        assertEquals(result[10].getLowerEnvelope(), toBigDecimal(-52.0240684705));
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getIndicatorValue(), toBigDecimal(-31.8992603228));
        assertEquals(result[13].getUpperEnvelope(), toBigDecimal(62.7580842491));
        assertEquals(result[13].getLowerEnvelope(), toBigDecimal(-62.7580842491));
        assertEquals(result[27].getTime(), of(2018, 3, 24, 0, 0));
        assertEquals(result[27].getIndicatorValue(), toBigDecimal(24.9255401437));
        assertEquals(result[27].getUpperEnvelope(), toBigDecimal(37.9213509837));
        assertEquals(result[27].getLowerEnvelope(), toBigDecimal(-37.9213509837));
        assertEquals(result[52].getTime(), of(2018, 4, 18, 0, 0));
        assertEquals(result[52].getIndicatorValue(), toBigDecimal(49.5118031054));
        assertEquals(result[52].getUpperEnvelope(), toBigDecimal(40.2829662758));
        assertEquals(result[52].getLowerEnvelope(), toBigDecimal(-40.2829662758));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-24.9278571228));
        assertEquals(result[72].getUpperEnvelope(), toBigDecimal(20.6697401035));
        assertEquals(result[72].getLowerEnvelope(), toBigDecimal(-20.6697401035));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {RAINBOW_OSCILLATOR}, size: {0}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .period(2)
                .highLowLookBack(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {RAINBOW_OSCILLATOR}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .period(2)
                .highLowLookBack(10)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {RAINBOW_OSCILLATOR}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(new Tick[100])
                .period(2)
                .highLowLookBack(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {RAINBOW_OSCILLATOR}, period: {-2}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(-2)
                .highLowLookBack(10)
                .build()).getResult();
    }

    @Test
    public void highLowLookBackLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {RAINBOW_OSCILLATOR}, period: {-10}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(2)
                .highLowLookBack(-10)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RAINBOW_OSCILLATOR}, period: {110}, size: {100}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(12)
                .highLowLookBack(10)
                .build()).getResult();
    }

    @Test
    public void highLowLookBackMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RAINBOW_OSCILLATOR}, period: {12}, size: {11}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(new Tick[11])
                .priceType(CLOSE)
                .period(2)
                .highLowLookBack(12)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return RORequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .period(2)
                .highLowLookBack(10)
                .build();
    }

}
