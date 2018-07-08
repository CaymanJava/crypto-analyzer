package pro.crypto.indicator.ac;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class AccelerationDecelerationOscillatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAccelerationDecelerationOscillator() {
        ACResult[] result = new AccelerationDecelerationOscillator(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getIncreased());
        assertNull(result[36].getIndicatorValue());
        assertNull(result[36].getIncreased());
        assertEquals(result[37].getTime(), of(2018, 4, 3, 0, 0));
        assertEquals(result[37].getIndicatorValue(), toBigDecimal(17.2272410588));
        assertFalse(result[37].getIncreased());
        assertEquals(result[44].getTime(), of(2018, 4, 10, 0, 0));
        assertEquals(result[44].getIndicatorValue(), toBigDecimal(12.6016059412));
        assertTrue(result[44].getIncreased());
        assertEquals(result[47].getTime(), of(2018, 4, 13, 0, 0));
        assertEquals(result[47].getIndicatorValue(), toBigDecimal(19.3762125882));
        assertFalse(result[47].getIncreased());
        assertEquals(result[69].getTime(), of(2018, 5, 5, 0, 0));
        assertEquals(result[69].getIndicatorValue(), toBigDecimal(-39.5137877059));
        assertTrue(result[69].getIncreased());
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-19.5161944118));
        assertTrue(result[72].getIncreased());
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ACCELERATION_DECELERATION_OSCILLATOR}, size: {0}}");
        new AccelerationDecelerationOscillator(ACRequest.builder()
                .originalData(new Tick[0])
                .slowPeriod(5)
                .fastPeriod(34)
                .smoothedPeriod(5)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ACCELERATION_DECELERATION_OSCILLATOR}}");
        new AccelerationDecelerationOscillator(ACRequest.builder()
                .originalData(null)
                .slowPeriod(5)
                .fastPeriod(34)
                .smoothedPeriod(5)
                .build()).getResult();
    }

    @Test
    public void originalDataSizeLessThanSlowPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ACCELERATION_DECELERATION_OSCILLATOR}, period: {5}, size: {4}}");
        new AccelerationDecelerationOscillator(ACRequest.builder()
                .originalData(new Tick[4])
                .slowPeriod(5)
                .fastPeriod(34)
                .smoothedPeriod(5)
                .build()).getResult();
    }

    @Test
    public void originalDataSizeLessThanFastPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ACCELERATION_DECELERATION_OSCILLATOR}, period: {34}, size: {33}}");
        new AccelerationDecelerationOscillator(ACRequest.builder()
                .originalData(new Tick[33])
                .slowPeriod(5)
                .fastPeriod(34)
                .smoothedPeriod(5)
                .build()).getResult();
    }

    @Test
    public void originalDataSizeLessThanSmoothedPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ACCELERATION_DECELERATION_OSCILLATOR}, period: {39}, size: {38}}");
        new AccelerationDecelerationOscillator(ACRequest.builder()
                .originalData(new Tick[38])
                .slowPeriod(5)
                .fastPeriod(34)
                .smoothedPeriod(5)
                .build()).getResult();
    }

    @Test
    public void slowPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ACCELERATION_DECELERATION_OSCILLATOR}, period: {-5}}");
        new AccelerationDecelerationOscillator(ACRequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(-5)
                .fastPeriod(34)
                .smoothedPeriod(5)
                .build()).getResult();
    }

    @Test
    public void fastPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ACCELERATION_DECELERATION_OSCILLATOR}, period: {-34}}");
        new AccelerationDecelerationOscillator(ACRequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(5)
                .fastPeriod(-34)
                .smoothedPeriod(5)
                .build()).getResult();
    }

    @Test
    public void smoothedPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ACCELERATION_DECELERATION_OSCILLATOR}, period: {-5}}");
        new AccelerationDecelerationOscillator(ACRequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(5)
                .fastPeriod(34)
                .smoothedPeriod(-5)
                .build()).getResult();
    }

    private IndicatorRequest buildRequest() {
        return ACRequest.builder()
                .originalData(originalData)
                .slowPeriod(5)
                .fastPeriod(34)
                .smoothedPeriod(5)
                .build();
    }

}