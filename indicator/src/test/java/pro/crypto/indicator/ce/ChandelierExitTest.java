package pro.crypto.indicator.ce;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class ChandelierExitTest extends IndicatorAbstractTest {

    @Test
    public void testChandelierExitWithDefaultParameters() {
        IndicatorResult[] expectedResult = loadExpectedResult("chandelier_exit.json", CEResult[].class);
        CEResult[] actualResult = new ChandelierExit(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CHANDELIER_EXIT}, size: {0}}");
        new ChandelierExit(CERequest.builder()
                .originalData(new Tick[0])
                .period(22)
                .longFactor(3)
                .shortFactor(3)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CHANDELIER_EXIT}}");
        new ChandelierExit(CERequest.builder()
                .originalData(null)
                .period(22)
                .longFactor(3)
                .shortFactor(3)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CHANDELIER_EXIT}, period: {22}, size: {21}}");
        new ChandelierExit(CERequest.builder()
                .originalData(new Tick[21])
                .period(22)
                .longFactor(3)
                .shortFactor(3)
                .build()).getResult();
    }

    @Test
    public void longFactorLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Chandelier exit long factor should be more than 0 {indicator: {CHANDELIER_EXIT}, shift: {-3.00}}");
        new ChandelierExit(CERequest.builder()
                .originalData(new Tick[100])
                .period(22)
                .longFactor(-3)
                .shortFactor(3)
                .build()).getResult();
    }

    @Test
    public void shortFactorLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Chandelier exit short factor should be more than 0 {indicator: {CHANDELIER_EXIT}, shift: {-3.00}}");
        new ChandelierExit(CERequest.builder()
                .originalData(new Tick[100])
                .period(22)
                .longFactor(3)
                .shortFactor(-3)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHANDELIER_EXIT}, period: {-22}}");
        new ChandelierExit(CERequest.builder()
                .originalData(new Tick[100])
                .period(-22)
                .longFactor(3)
                .shortFactor(3)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return CERequest.builder()
                .originalData(originalData)
                .period(22)
                .longFactor(3)
                .shortFactor(3)
                .build();
    }

}
