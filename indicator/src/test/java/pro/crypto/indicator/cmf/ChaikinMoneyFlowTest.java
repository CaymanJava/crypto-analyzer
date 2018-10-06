package pro.crypto.indicator.cmf;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class ChaikinMoneyFlowTest extends IndicatorAbstractTest {

    @Test
    public void testCMFTwentyOneDaysPeriod() {
        IndicatorResult[] expectedResult = loadExpectedResult("chaikin_money_flow.json", CMFResult[].class);
        CMFResult[] actualResult = new ChaikinMoneyFlow(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CHAIKIN_MONEY_FLOW}, size: {0}}");
        new ChaikinMoneyFlow(CMFRequest.builder()
                .originalData(new Tick[0])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CHAIKIN_MONEY_FLOW}}");
        new ChaikinMoneyFlow(CMFRequest.builder()
                .originalData(null)
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CHAIKIN_MONEY_FLOW}, period: {20}, size: {19}}");
        new ChaikinMoneyFlow(CMFRequest.builder()
                .originalData(new Tick[19])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHAIKIN_MONEY_FLOW}, period: {-20}}");
        new ChaikinMoneyFlow(CMFRequest.builder()
                .originalData(new Tick[19])
                .period(-20)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return CMFRequest.builder()
                .originalData(originalData)
                .period(21)
                .build();
    }

}
