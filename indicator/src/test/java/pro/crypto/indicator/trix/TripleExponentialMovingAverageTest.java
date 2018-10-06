package pro.crypto.indicator.trix;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class TripleExponentialMovingAverageTest extends IndicatorAbstractTest {

    @Test
    public void testTRIXWithPeriodEighteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("triple_exponential_moving_average_1.json", TRIXResult[].class);
        TRIXResult[] actualResult = new TripleExponentialMovingAverage(buildRequest(18)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testTRIXWithPeriodFourteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("triple_exponential_moving_average_2.json", TRIXResult[].class);
        TRIXResult[] actualResult = new TripleExponentialMovingAverage(buildRequest(14)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}, size: {0}}");
        new TripleExponentialMovingAverage(TRIXRequest.builder()
                .originalData(new Tick[0])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}}");
        new TripleExponentialMovingAverage(TRIXRequest.builder()
                .originalData(null)
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}," +
                " period: {70}, size: {69}}");
        new TripleExponentialMovingAverage(TRIXRequest.builder()
                .originalData(new Tick[69])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}, period: {-14}");
        new TripleExponentialMovingAverage(TRIXRequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .build()).getResult();
    }

    @Override
    protected TRIXRequest buildRequest() {
        return TRIXRequest.builder()
                .originalData(originalData)
                .build();
    }

    private IndicatorRequest buildRequest(int period) {
        TRIXRequest request = buildRequest();
        request.setPeriod(period);
        return request;
    }

}
