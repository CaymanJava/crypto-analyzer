package pro.crypto.indicators.stoch;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.StochRequest;
import pro.crypto.model.result.StochResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.MODIFIED_MOVING_AVERAGE;

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
        assertTrue(isNull(result[0].getFastStochastic()));
        assertTrue(isNull(result[0].getSlowStochastic()));
        assertTrue(isNull(result[10].getFastStochastic()));
        assertTrue(isNull(result[10].getSlowStochastic()));
        assertTrue(isNull(result[14].getFastStochastic()));
        assertTrue(isNull(result[14].getSlowStochastic()));
        assertEquals(result[15].getTime(), of(2018, 3, 12, 0, 0));
        assertEquals(result[15].getFastStochastic(), new BigDecimal(5.1774560933).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[15].getSlowStochastic()));
        assertEquals(result[17].getTime(), of(2018, 3, 14, 0, 0));
        assertEquals(result[17].getFastStochastic(), new BigDecimal(23.5114430784).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[17].getSlowStochastic(), new BigDecimal(15.4053965828).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getFastStochastic(), new BigDecimal(51.0711126238).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[28].getSlowStochastic(), new BigDecimal(26.7877943637).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getFastStochastic(), new BigDecimal(85.6660248578).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getSlowStochastic(), new BigDecimal(70.2792866213).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getFastStochastic(), new BigDecimal(89.7247978764).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getSlowStochastic(), new BigDecimal(90.2357825983).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getFastStochastic(), new BigDecimal(9.8491602516).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getSlowStochastic(), new BigDecimal(20.6941211114).setScale(10, BigDecimal.ROUND_HALF_UP));
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

    private StochRequest buildRequest() {
        return StochRequest.builder()
                .originalData(originalData)
                .fastPeriod(14)
                .slowPeriod(3)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

}