package pro.crypto.indicator.co;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class ChaikinOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testChaikinOscillatorWithPeriodsThreeAndTen() {
        IndicatorResult[] expectedResult = loadExpectedResult("chaikin_oscillator_1.json", COResult[].class);
        COResult[] actualResult = new ChaikinOscillator(buildRequest(10, 3)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testChaikinOscillatorWithPeriodsSixAndTwenty() {
        IndicatorResult[] expectedResult = loadExpectedResult("chaikin_oscillator_2.json", COResult[].class);
        COResult[] actualResult = new ChaikinOscillator(buildRequest(20, 6)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CHAIKIN_OSCILLATOR}, size: {0}}");
        new ChaikinOscillator(CORequest.builder()
                .originalData(new Tick[0])
                .slowPeriod(3)
                .fastPeriod(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CHAIKIN_OSCILLATOR}}");
        new ChaikinOscillator(CORequest.builder()
                .originalData(null)
                .slowPeriod(3)
                .fastPeriod(10)
                .build()).getResult();
    }

    @Test
    public void emptySlowPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHAIKIN_OSCILLATOR}, period: {0}}");
        new ChaikinOscillator(CORequest.builder()
                .originalData(new Tick[30])
                .fastPeriod(10)
                .build()).getResult();
    }

    @Test
    public void emptyFastPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHAIKIN_OSCILLATOR}, period: {0}}");
        new ChaikinOscillator(CORequest.builder()
                .originalData(new Tick[30])
                .slowPeriod(10)
                .build()).getResult();
    }

    @Test
    public void notEnoughDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CHAIKIN_OSCILLATOR}, period: {10}, size: {5}}");
        new ChaikinOscillator(CORequest.builder()
                .originalData(new Tick[5])
                .slowPeriod(3)
                .fastPeriod(10)
                .build()).getResult();
    }

    @Override
    protected CORequest buildRequest() {
        return CORequest.builder()
                .originalData(originalData)
                .build();
    }

    private CORequest buildRequest(int fastPeriod, int slowPeriod) {
        CORequest request = buildRequest();
        request.setFastPeriod(fastPeriod);
        request.setSlowPeriod(slowPeriod);
        return request;
    }

}
