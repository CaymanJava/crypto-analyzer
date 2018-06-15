package pro.crypto.indicators.kvo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.KVORequest;
import pro.crypto.model.result.KVOResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class KlingerVolumeOscillatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testKlingerVolumeOscillator() {
        KVOResult[] result = new KlingerVolumeOscillator(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineValue());
        assertNull(result[24].getIndicatorValue());
        assertNull(result[24].getSignalLineValue());
        assertEquals(result[25].getTime(), of(2018, 3, 22, 0, 0));
        assertEquals(result[25].getIndicatorValue(), toBigDecimal(-8.6691769306));
        assertNull(result[25].getSignalLineValue());
        assertEquals(result[37].getTime(), of(2018, 4, 3, 0, 0));
        assertEquals(result[37].getIndicatorValue(), toBigDecimal(22.3617101014));
        assertEquals(result[37].getSignalLineValue(), toBigDecimal(8.3023277073));
        assertEquals(result[52].getTime(), of(2018, 4, 18, 0, 0));
        assertEquals(result[52].getIndicatorValue(), toBigDecimal(-1.0531088942));
        assertEquals(result[52].getSignalLineValue(), toBigDecimal(4.5601647149));
        assertEquals(result[65].getTime(), of(2018, 5, 1, 0, 0));
        assertEquals(result[65].getIndicatorValue(), toBigDecimal(-1.3198021570));
        assertEquals(result[65].getSignalLineValue(), toBigDecimal(8.0932549425));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-7.9427379336));
        assertEquals(result[72].getSignalLineValue(), toBigDecimal(-4.0894385560));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {KLINGER_VOLUME_OSCILLATOR}, size: {0}}");
        new KlingerVolumeOscillator(KVORequest.builder()
                .originalData(new Tick[0])
                .shortPeriod(14)
                .longPeriod(25)
                .signalPeriod(13)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {KLINGER_VOLUME_OSCILLATOR}}");
        new KlingerVolumeOscillator(KVORequest.builder()
                .originalData(null)
                .shortPeriod(14)
                .longPeriod(25)
                .signalPeriod(13)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {KLINGER_VOLUME_OSCILLATOR}, period: {52}, size: {51}}");
        new KlingerVolumeOscillator(KVORequest.builder()
                .originalData(new Tick[51])
                .shortPeriod(14)
                .longPeriod(25)
                .signalPeriod(13)
                .build()).getResult();
    }

    @Test
    public void shortPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KLINGER_VOLUME_OSCILLATOR}, period: {-14}}");
        new KlingerVolumeOscillator(KVORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(-14)
                .longPeriod(25)
                .signalPeriod(13)
                .build()).getResult();
    }

    @Test
    public void longPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KLINGER_VOLUME_OSCILLATOR}, period: {-25}}");
        new KlingerVolumeOscillator(KVORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(14)
                .longPeriod(-25)
                .signalPeriod(13)
                .build()).getResult();
    }

    @Test
    public void signalLinePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KLINGER_VOLUME_OSCILLATOR}, period: {-13}}");
        new KlingerVolumeOscillator(KVORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(14)
                .longPeriod(25)
                .signalPeriod(-13)
                .build()).getResult();
    }

    private KVORequest buildRequest() {
        return KVORequest.builder()
                .originalData(originalData)
                .shortPeriod(14)
                .longPeriod(25)
                .signalPeriod(13)
                .build();
    }

}