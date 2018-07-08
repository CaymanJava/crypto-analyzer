package pro.crypto.indicator.stoch;

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
import static pro.crypto.model.IndicatorType.*;

public class PreferableStochasticOscillatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testPreferableStochasticWithPeriodFourteenAndExponentialMovingAverage() {
        StochResult[] result = new PreferableStochasticOscillator(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getFastStochastic());
        assertNull(result[0].getSlowStochastic());
        assertNull(result[10].getFastStochastic());
        assertNull(result[10].getSlowStochastic());
        assertNull(result[14].getFastStochastic());
        assertNull(result[14].getSlowStochastic());
        assertEquals(result[15].getTime(), of(2018, 3, 12, 0, 0));
        assertEquals(result[15].getFastStochastic(), toBigDecimal(5.1774560918));
        assertNull(result[15].getSlowStochastic());
        assertEquals(result[17].getTime(), of(2018, 3, 14, 0, 0));
        assertEquals(result[17].getFastStochastic(), toBigDecimal(23.5114430781));
        assertEquals(result[17].getSlowStochastic(), toBigDecimal(15.4053965825));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getFastStochastic(), toBigDecimal(51.0711126231));
        assertEquals(result[28].getSlowStochastic(), toBigDecimal(26.7877943631));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getFastStochastic(), toBigDecimal(85.6660248579));
        assertEquals(result[32].getSlowStochastic(), toBigDecimal(70.2792866213));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getFastStochastic(), toBigDecimal(89.7247978773));
        assertEquals(result[45].getSlowStochastic(), toBigDecimal(90.2357825985));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getFastStochastic(), toBigDecimal(9.8491602527));
        assertEquals(result[72].getSlowStochastic(), toBigDecimal(20.6941211124));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {PREFERABLE_STOCHASTIC_OSCILLATOR}, size: {0}}");
        new PreferableStochasticOscillator(StochRequest.builder()
                .originalData(new Tick[0])
                .fastPeriod(14)
                .slowPeriod(3)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {PREFERABLE_STOCHASTIC_OSCILLATOR}}");
        new PreferableStochasticOscillator(StochRequest.builder()
                .originalData(null)
                .fastPeriod(14)
                .slowPeriod(3)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {PREFERABLE_STOCHASTIC_OSCILLATOR}, period: {26}, size: {21}}");
        new PreferableStochasticOscillator(StochRequest.builder()
                .originalData(new Tick[21])
                .fastPeriod(14)
                .slowPeriod(6)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void fastPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PREFERABLE_STOCHASTIC_OSCILLATOR}, period: {-14}}");
        new PreferableStochasticOscillator(StochRequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(-14)
                .slowPeriod(6)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void slowPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PREFERABLE_STOCHASTIC_OSCILLATOR}, period: {-3}}");
        new PreferableStochasticOscillator(StochRequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(14)
                .slowPeriod(-3)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {PREFERABLE_STOCHASTIC_OSCILLATOR}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new PreferableStochasticOscillator(StochRequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(14)
                .slowPeriod(3)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .build()).getResult();
    }

    private IndicatorRequest buildRequest() {
        return StochRequest.builder()
                .originalData(originalData)
                .fastPeriod(14)
                .slowPeriod(3)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

}