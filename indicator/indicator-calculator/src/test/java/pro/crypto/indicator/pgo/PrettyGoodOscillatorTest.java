package pro.crypto.indicator.pgo;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class PrettyGoodOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testPrettyGoodOscillatorWithPeriodFourteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("pretty_good_oscillator.json", PGOResult[].class);
        PGOResult[] actualResult = new PrettyGoodOscillator(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {PRETTY_GOOD_OSCILLATOR}, size: {0}}");
        new PrettyGoodOscillator(PGORequest.builder()
                .originalData(new Tick[0])
                .period(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {PRETTY_GOOD_OSCILLATOR}}");
        new PrettyGoodOscillator(PGORequest.builder()
                .originalData(null)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {PRETTY_GOOD_OSCILLATOR}, period: {28}, size: {27}}");
        new PrettyGoodOscillator(PGORequest.builder()
                .originalData(new Tick[27])
                .period(14)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PRETTY_GOOD_OSCILLATOR}, period: {-14}");
        new PrettyGoodOscillator(PGORequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return PGORequest.builder()
                .originalData(originalData)
                .period(14)
                .build();
    }

}
