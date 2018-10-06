package pro.crypto.indicator.ac;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class AccelerationDecelerationOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testAccelerationDecelerationOscillator() {
        IndicatorResult[] expectedResult = loadExpectedResult("acceleration_deceleration_oscillator.json", ACResult[].class);
        ACResult[] actualResult = new AccelerationDecelerationOscillator(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
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

    @Override
    protected IndicatorRequest buildRequest() {
        return ACRequest.builder()
                .originalData(originalData)
                .slowPeriod(5)
                .fastPeriod(34)
                .smoothedPeriod(5)
                .build();
    }

}