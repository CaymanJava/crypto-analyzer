package pro.crypto.indicator.ro;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RainbowOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testRainbowOscillator() {
        IndicatorResult[] expectedResult = loadExpectedResult("rainbow_oscillator.json", ROResult[].class);
        ROResult[] actualResult = new RainbowOscillator(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {RAINBOW_OSCILLATOR}, size: {0}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .period(2)
                .highLowLookBack(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {RAINBOW_OSCILLATOR}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .period(2)
                .highLowLookBack(10)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {RAINBOW_OSCILLATOR}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(new Tick[100])
                .period(2)
                .highLowLookBack(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {RAINBOW_OSCILLATOR}, period: {-2}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(-2)
                .highLowLookBack(10)
                .build()).getResult();
    }

    @Test
    public void highLowLookBackLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {RAINBOW_OSCILLATOR}, period: {-10}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(2)
                .highLowLookBack(-10)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RAINBOW_OSCILLATOR}, period: {110}, size: {100}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(12)
                .highLowLookBack(10)
                .build()).getResult();
    }

    @Test
    public void highLowLookBackMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RAINBOW_OSCILLATOR}, period: {12}, size: {11}}");
        new RainbowOscillator(RORequest.builder()
                .originalData(new Tick[11])
                .priceType(CLOSE)
                .period(2)
                .highLowLookBack(12)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return RORequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .period(2)
                .highLowLookBack(10)
                .build();
    }

}
