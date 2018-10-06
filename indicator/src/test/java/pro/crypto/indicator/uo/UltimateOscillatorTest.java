package pro.crypto.indicator.uo;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class UltimateOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testUltimateOscillatorWithDefaultPeriods() {
        IndicatorResult[] expectedResult = loadExpectedResult("ultimate_oscillator.json", UOResult[].class);
        UOResult[] actualResult = new UltimateOscillator(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ULTIMATE_OSCILLATOR}, size: {0}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[0])
                .shortPeriod(7)
                .middlePeriod(14)
                .longPeriod(28)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ULTIMATE_OSCILLATOR}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(null)
                .shortPeriod(7)
                .middlePeriod(14)
                .longPeriod(28)
                .build()).getResult();
    }

    @Test
    public void shortPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ULTIMATE_OSCILLATOR}, period: {-7}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(-7)
                .middlePeriod(14)
                .longPeriod(28)
                .build()).getResult();
    }

    @Test
    public void middlePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ULTIMATE_OSCILLATOR}, period: {-14}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(7)
                .middlePeriod(-14)
                .longPeriod(28)
                .build()).getResult();
    }

    @Test
    public void longPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ULTIMATE_OSCILLATOR}, period: {-28}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(7)
                .middlePeriod(14)
                .longPeriod(-28)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ULTIMATE_OSCILLATOR}, period: {29}, size: {19}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[19])
                .shortPeriod(7)
                .middlePeriod(14)
                .longPeriod(28)
                .build()).getResult();
    }

    @Test
    public void incorrectShortPeriodLengthTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incorrect period values {indicator: {ULTIMATE_OSCILLATOR}, shortPeriod: {16}, middlePeriod: {14}}, longPeriod: {28}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(16)
                .middlePeriod(14)
                .longPeriod(28)
                .build()).getResult();
    }

    @Test
    public void incorrectMiddlePeriodLengthTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incorrect period values {indicator: {ULTIMATE_OSCILLATOR}, shortPeriod: {7}, middlePeriod: {29}}, longPeriod: {28}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(7)
                .middlePeriod(29)
                .longPeriod(28)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return UORequest.builder()
                .originalData(originalData)
                .shortPeriod(7)
                .middlePeriod(14)
                .longPeriod(28)
                .build();
    }

}
