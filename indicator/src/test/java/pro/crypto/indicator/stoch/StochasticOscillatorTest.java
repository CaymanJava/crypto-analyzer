package pro.crypto.indicator.stoch;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.MODIFIED_MOVING_AVERAGE;

public class StochasticOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testStochasticOscillatorWithPeriodFourteenAndModifiedMovingAverage() {
        IndicatorResult[] expectedResult = loadExpectedResult("stochastic_oscillator.json", StochResult[].class);
        StochResult[] actualResult = new StochasticOscillator(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
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

    @Override
    protected IndicatorRequest buildRequest() {
        return StochRequest.builder()
                .originalData(originalData)
                .fastPeriod(14)
                .slowPeriod(3)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build();
    }

}
