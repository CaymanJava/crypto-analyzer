package pro.crypto.indicator.ao;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class AwesomeOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testAwesomeOscillator() {
        IndicatorResult[] expectedResult = loadExpectedResult("awesome_oscillator.json", AOResult[].class);
        AOResult[] actualResult = new AwesomeOscillator(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {AWESOME_OSCILLATOR}, size: {0}}");
        new AwesomeOscillator(AORequest.builder()
                .originalData(new Tick[0])
                .fastPeriod(5)
                .slowPeriod(34)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {AWESOME_OSCILLATOR}}");
        new AwesomeOscillator(AORequest.builder()
                .originalData(null)
                .fastPeriod(5)
                .slowPeriod(34)
                .build()).getResult();
    }

    @Test
    public void originalDataSizeLessThanFastPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {AWESOME_OSCILLATOR}, period: {5}, size: {4}}");
        new AwesomeOscillator(AORequest.builder()
                .originalData(new Tick[4])
                .fastPeriod(5)
                .slowPeriod(34)
                .build()).getResult();
    }

    @Test
    public void originalDataSizeLessThanSlowPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {AWESOME_OSCILLATOR}, period: {34}, size: {33}}");
        new AwesomeOscillator(AORequest.builder()
                .originalData(new Tick[33])
                .fastPeriod(5)
                .slowPeriod(34)
                .build()).getResult();
    }

    @Test
    public void fastPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {AWESOME_OSCILLATOR}, period: {-5}}");
        new AwesomeOscillator(AORequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(-5)
                .slowPeriod(34)
                .build()).getResult();
    }

    @Test
    public void slowPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {AWESOME_OSCILLATOR}, period: {-34}}");
        new AwesomeOscillator(AORequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(5)
                .slowPeriod(-34)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return AORequest.builder()
                .originalData(originalData)
                .fastPeriod(5)
                .slowPeriod(34)
                .build();
    }

}
