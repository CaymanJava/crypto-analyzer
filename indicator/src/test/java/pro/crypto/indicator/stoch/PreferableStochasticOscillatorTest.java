package pro.crypto.indicator.stoch;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.*;

public class PreferableStochasticOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testPreferableStochasticWithPeriodFourteenAndExponentialMovingAverage() {
        IndicatorResult[] expectedResult = loadExpectedResult("preferable_stochastic_oscillator.json", StochResult[].class);
        StochResult[] actualResult = new PreferableStochasticOscillator(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {PREFERABLE_STOCHASTIC_OSCILLATOR}, size: {0}}");
        new PreferableStochasticOscillator(StochRequest.builder()
                .originalData(new Tick[0])
                .fastStochPeriod(14)
                .slowStochPeriod(3)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {PREFERABLE_STOCHASTIC_OSCILLATOR}}");
        new PreferableStochasticOscillator(StochRequest.builder()
                .originalData(null)
                .fastStochPeriod(14)
                .slowStochPeriod(3)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {PREFERABLE_STOCHASTIC_OSCILLATOR}, period: {26}, size: {21}}");
        new PreferableStochasticOscillator(StochRequest.builder()
                .originalData(new Tick[21])
                .fastStochPeriod(14)
                .slowStochPeriod(6)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void fastPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PREFERABLE_STOCHASTIC_OSCILLATOR}, period: {-14}}");
        new PreferableStochasticOscillator(StochRequest.builder()
                .originalData(new Tick[100])
                .fastStochPeriod(-14)
                .slowStochPeriod(6)
                .movingAverageType(MODIFIED_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void slowPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PREFERABLE_STOCHASTIC_OSCILLATOR}, period: {-3}}");
        new PreferableStochasticOscillator(StochRequest.builder()
                .originalData(new Tick[100])
                .fastStochPeriod(14)
                .slowStochPeriod(-3)
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
                .fastStochPeriod(14)
                .slowStochPeriod(3)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return StochRequest.builder()
                .originalData(originalData)
                .fastStochPeriod(14)
                .slowStochPeriod(3)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

}
