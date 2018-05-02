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
import static pro.crypto.model.IndicatorType.MODIFIED_MOVING_AVERAGE;

public class StochasticOscillatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testStochasticOscillatorWithPeriodFourteenAndModifiedMovingAverage() {
        StochResult[] result = new StochasticOscillator(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getFastStochastic()));
        assertTrue(isNull(result[0].getSlowStochastic()));
        assertTrue(isNull(result[10].getFastStochastic()));
        assertTrue(isNull(result[10].getSlowStochastic()));
        assertTrue(isNull(result[12].getFastStochastic()));
        assertTrue(isNull(result[12].getSlowStochastic()));
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getFastStochastic(), new BigDecimal(3.7382485976).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[13].getSlowStochastic()));
        assertEquals(result[15].getTime(), of(2018, 3, 12, 0, 0));
        assertEquals(result[15].getFastStochastic(), new BigDecimal(7.3668854850).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[15].getSlowStochastic(), new BigDecimal(5.1774560918).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getFastStochastic(), new BigDecimal(77.7701517578).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[28].getSlowStochastic(), new BigDecimal(38.9215214551).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getFastStochastic(), new BigDecimal(93.7500000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getSlowStochastic(), new BigDecimal(77.9710889173).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getFastStochastic(), new BigDecimal(88.6727962826).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getSlowStochastic(), new BigDecimal(89.9802821872).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getFastStochastic(), new BigDecimal(0.0314073910).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getSlowStochastic(), new BigDecimal(15.2716406824).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {STOCHASTIC_OSCILLATOR}, size: {0}}");
        new StochasticOscillator(StochRequest.builder()
                .originalData(new Tick[0])
                .fastPeriod(14)
                .slowPeriod(3)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {STOCHASTIC_OSCILLATOR}}");
        new StochasticOscillator(StochRequest.builder()
                .originalData(null)
                .fastPeriod(14)
                .slowPeriod(3)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {STOCHASTIC_OSCILLATOR}, period: {20}, size: {19}}");
        new StochasticOscillator(StochRequest.builder()
                .originalData(new Tick[19])
                .fastPeriod(14)
                .slowPeriod(6)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void fastPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {STOCHASTIC_OSCILLATOR}, period: {-14}}");
        new StochasticOscillator(StochRequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(-14)
                .slowPeriod(6)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void slowPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {STOCHASTIC_OSCILLATOR}, period: {-3}}");
        new StochasticOscillator(StochRequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(14)
                .slowPeriod(-3)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {STOCHASTIC_OSCILLATOR}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new StochasticOscillator(StochRequest.builder()
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
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build();
    }

}