package pro.crypto.indicator.imi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class IntradayMomentumIndexTest extends IndicatorAbstractTest {

    @Test
    public void testIntradayMomentumIndexWithPeriodTwenty() {
        IndicatorResult[] expectedResult = loadExpectedResult("intraday_momentum_index.json", IMIResult[].class);
        IMIResult[] actualResult = new IntradayMomentumIndex(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {INTRADAY_MOMENTUM_INDEX}, size: {0}}");
        new IntradayMomentumIndex(IMIRequest.builder()
                .originalData(new Tick[0])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {INTRADAY_MOMENTUM_INDEX}}");
        new IntradayMomentumIndex(IMIRequest.builder()
                .originalData(null)
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {INTRADAY_MOMENTUM_INDEX}, period: {20}, size: {19}}");
        new IntradayMomentumIndex(IMIRequest.builder()
                .originalData(new Tick[19])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {INTRADAY_MOMENTUM_INDEX}, period: {-20}");
        new IntradayMomentumIndex(IMIRequest.builder()
                .originalData(new Tick[100])
                .period(-20)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return IMIRequest.builder()
                .originalData(originalData)
                .period(20)
                .build();
    }

}
