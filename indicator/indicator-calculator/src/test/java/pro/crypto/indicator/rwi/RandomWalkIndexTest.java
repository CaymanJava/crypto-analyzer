package pro.crypto.indicator.rwi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class RandomWalkIndexTest extends IndicatorAbstractTest {

    @Test
    public void testRandomWalkIndexWithPeriodFourteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("random_walk_index_1.json", RWIResult[].class);
        RWIResult[] actualResult = new RandomWalkIndex(buildRequest(14)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testRandomWalkIndexWithPeriodTen() {
        IndicatorResult[] expectedResult = loadExpectedResult("random_walk_index_2.json", RWIResult[].class);
        RWIResult[] actualResult = new RandomWalkIndex(buildRequest(10)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {RANDOM_WALK_INDEX}, size: {0}}");
        new RandomWalkIndex(RWIRequest.builder()
                .originalData(new Tick[0])
                .period(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {RANDOM_WALK_INDEX}}");
        new RandomWalkIndex(RWIRequest.builder()
                .originalData(null)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {RANDOM_WALK_INDEX}, period: {-14}}");
        new RandomWalkIndex(RWIRequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RANDOM_WALK_INDEX}, period: {28}, size: {27}}");
        new RandomWalkIndex(RWIRequest.builder()
                .originalData(new Tick[27])
                .period(14)
                .build()).getResult();
    }

    @Override
    protected RWIRequest buildRequest() {
        return RWIRequest.builder()
                .originalData(originalData)
                .build();
    }

    private IndicatorRequest buildRequest(int period) {
        RWIRequest request = buildRequest();
        request.setPeriod(period);
        return request;
    }

}
