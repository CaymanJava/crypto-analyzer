package pro.crypto.indicator.tmf;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class TwiggsMoneyFlowTest extends IndicatorAbstractTest {

    @Test
    public void testTwiggsMoneyFlowWithPeriodTwentyOne() {
        IndicatorResult[] expectedResult = loadExpectedResult("twiggs_money_flow.json", TMFResult[].class);
        TMFResult[] actualResult = new TwiggsMoneyFlow(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {TWIGGS_MONEY_FLOW}, size: {0}}");
        new TwiggsMoneyFlow(TMFRequest.builder()
                .originalData(new Tick[0])
                .period(21)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {TWIGGS_MONEY_FLOW}}");
        new TwiggsMoneyFlow(TMFRequest.builder()
                .originalData(null)
                .period(21)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {TWIGGS_MONEY_FLOW}, period: {21}, size: {20}}");
        new TwiggsMoneyFlow(TMFRequest.builder()
                .originalData(new Tick[20])
                .period(21)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {TWIGGS_MONEY_FLOW}, period: {-21}}");
        new TwiggsMoneyFlow(TMFRequest.builder()
                .originalData(new Tick[100])
                .period(-21)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return TMFRequest.builder()
                .originalData(originalData)
                .period(21)
                .build();
    }

}
