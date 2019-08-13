package pro.crypto.indicator.cmo;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;

public class ChandeMomentumOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testChandeMomentumOscillatorWithPeriodNine() {
        IndicatorResult[] expectedResult = loadExpectedResult("chande_momentum_oscillator.json", CMOResult[].class);
        CMOResult[] actualResult = new ChandeMomentumOscillator(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CHANDE_MOMENTUM_OSCILLATOR}, size: {0}}");
        new ChandeMomentumOscillator(CMORequest.builder()
                .originalData(new Tick[0])
                .period(9)
                .signalLinePeriod(10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CHANDE_MOMENTUM_OSCILLATOR}}");
        new ChandeMomentumOscillator(CMORequest.builder()
                .originalData(null)
                .period(9)
                .signalLinePeriod(10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CHANDE_MOMENTUM_OSCILLATOR}, period: {19}, size: {18}}");
        new ChandeMomentumOscillator(CMORequest.builder()
                .originalData(new Tick[18])
                .period(9)
                .signalLinePeriod(10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHANDE_MOMENTUM_OSCILLATOR}, period: {-9}}");
        new ChandeMomentumOscillator(CMORequest.builder()
                .originalData(new Tick[100])
                .period(-9)
                .signalLinePeriod(10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void signalLinePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHANDE_MOMENTUM_OSCILLATOR}, period: {-10}}");
        new ChandeMomentumOscillator(CMORequest.builder()
                .originalData(new Tick[100])
                .period(9)
                .signalLinePeriod(-10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {CHANDE_MOMENTUM_OSCILLATOR}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new ChandeMomentumOscillator(CMORequest.builder()
                .originalData(new Tick[100])
                .period(9)
                .signalLinePeriod(10)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return CMORequest.builder()
                .originalData(originalData)
                .period(9)
                .signalLinePeriod(10)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

}
