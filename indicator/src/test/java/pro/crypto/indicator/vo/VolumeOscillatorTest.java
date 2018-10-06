package pro.crypto.indicator.vo;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class VolumeOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testVolumeOscillatorDefaultPeriods() {
        IndicatorResult[] expectedResult = loadExpectedResult("volume_oscillator.json", VOResult[].class);
        VOResult[] actualResult = new VolumeOscillator(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {VOLUME_OSCILLATOR}, size: {0}}");
        new VolumeOscillator(VORequest.builder()
                .originalData(new Tick[0])
                .shortPeriod(12)
                .longPeriod(26)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {VOLUME_OSCILLATOR}}");
        new VolumeOscillator(VORequest.builder()
                .originalData(null)
                .shortPeriod(12)
                .longPeriod(26)
                .build()).getResult();
    }

    @Test
    public void shortPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {VOLUME_OSCILLATOR}, period: {-12}}");
        new VolumeOscillator(VORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(-12)
                .longPeriod(26)
                .build()).getResult();
    }

    @Test
    public void longPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {VOLUME_OSCILLATOR}, period: {-26}}");
        new VolumeOscillator(VORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(12)
                .longPeriod(-26)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {VOLUME_OSCILLATOR}, period: {26}, size: {25}}");
        new VolumeOscillator(VORequest.builder()
                .originalData(new Tick[25])
                .shortPeriod(12)
                .longPeriod(26)
                .build()).getResult();
    }

    @Test
    public void incorrectShortPeriodLengthTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incorrect period values {indicator: {VOLUME_OSCILLATOR}, shortPeriod: {27}, longPeriod: {26}}");
        new VolumeOscillator(VORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(27)
                .longPeriod(26)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return VORequest.builder()
                .originalData(originalData)
                .shortPeriod(12)
                .longPeriod(26)
                .build();
    }

}
