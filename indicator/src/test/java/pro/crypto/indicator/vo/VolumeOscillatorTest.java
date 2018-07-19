package pro.crypto.indicator.vo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class VolumeOscillatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testVolumeOscillatorDefaultPeriods() {
        VOResult[] result = new VolumeOscillator(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[24].getIndicatorValue());
        assertEquals(result[25].getTime(), of(2018, 3, 22, 0, 0));
        assertEquals(result[25].getIndicatorValue(), toBigDecimal(24.9096145275));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(17.1765391057));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(7.3934271547));
        assertEquals(result[61].getTime(), of(2018, 4, 27, 0, 0));
        assertEquals(result[61].getIndicatorValue(), toBigDecimal(9.1827040418));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-19.8736742978));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {VOLUME_OSCILLATOR}, size: {0}}");
        new VolumeOscillator(VORequest.builder()
                .originalData(new Tick[0])
                .shortPeriod(12)
                .longPeriod(26)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {VOLUME_OSCILLATOR}}");
        new VolumeOscillator(VORequest.builder()
                .originalData(null)
                .shortPeriod(12)
                .longPeriod(26)
                .build()).getResult();
    }

    @Test
    public void shortPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {VOLUME_OSCILLATOR}, period: {-12}}");
        new VolumeOscillator(VORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(-12)
                .longPeriod(26)
                .build()).getResult();
    }

    @Test
    public void longPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {VOLUME_OSCILLATOR}, period: {-26}}");
        new VolumeOscillator(VORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(12)
                .longPeriod(-26)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {VOLUME_OSCILLATOR}, period: {26}, size: {25}}");
        new VolumeOscillator(VORequest.builder()
                .originalData(new Tick[25])
                .shortPeriod(12)
                .longPeriod(26)
                .build()).getResult();
    }

    @Test
    public void incorrectShortPeriodLengthTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incorrect period values {indicator: {VOLUME_OSCILLATOR}, shortPeriod: {27}, longPeriod: {26}}");
        new VolumeOscillator(VORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(27)
                .longPeriod(26)
                .build()).getResult();
    }

    private IndicatorRequest buildRequest() {
        return VORequest.builder()
                .originalData(originalData)
                .shortPeriod(12)
                .longPeriod(26)
                .build();
    }

}