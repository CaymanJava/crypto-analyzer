package pro.crypto.indicator.kvo;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class KlingerVolumeOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testKlingerVolumeOscillator() {
        IndicatorResult[] expectedResult = loadExpectedResult("klinger_volume_oscillator.json", KVOResult[].class);
        KVOResult[] actualResult = new KlingerVolumeOscillator(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {KLINGER_VOLUME_OSCILLATOR}, size: {0}}");
        new KlingerVolumeOscillator(KVORequest.builder()
                .originalData(new Tick[0])
                .shortPeriod(14)
                .longPeriod(25)
                .signalPeriod(13)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {KLINGER_VOLUME_OSCILLATOR}}");
        new KlingerVolumeOscillator(KVORequest.builder()
                .originalData(null)
                .shortPeriod(14)
                .longPeriod(25)
                .signalPeriod(13)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {KLINGER_VOLUME_OSCILLATOR}, period: {52}, size: {51}}");
        new KlingerVolumeOscillator(KVORequest.builder()
                .originalData(new Tick[51])
                .shortPeriod(14)
                .longPeriod(25)
                .signalPeriod(13)
                .build()).getResult();
    }

    @Test
    public void shortPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KLINGER_VOLUME_OSCILLATOR}, period: {-14}}");
        new KlingerVolumeOscillator(KVORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(-14)
                .longPeriod(25)
                .signalPeriod(13)
                .build()).getResult();
    }

    @Test
    public void longPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KLINGER_VOLUME_OSCILLATOR}, period: {-25}}");
        new KlingerVolumeOscillator(KVORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(14)
                .longPeriod(-25)
                .signalPeriod(13)
                .build()).getResult();
    }

    @Test
    public void signalLinePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KLINGER_VOLUME_OSCILLATOR}, period: {-13}}");
        new KlingerVolumeOscillator(KVORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(14)
                .longPeriod(25)
                .signalPeriod(-13)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return KVORequest.builder()
                .originalData(originalData)
                .shortPeriod(14)
                .longPeriod(25)
                .signalPeriod(13)
                .build();
    }

}
