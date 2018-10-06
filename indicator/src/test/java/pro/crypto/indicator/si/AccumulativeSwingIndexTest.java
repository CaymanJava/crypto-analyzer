package pro.crypto.indicator.si;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class AccumulativeSwingIndexTest extends IndicatorAbstractTest {

    @Test
    public void testASIWithLimitThree() {
        IndicatorResult[] expectedResult = loadExpectedResult("accumulative_swing_index_1.json", SIResult[].class);
        SIResult[] actualResult = new AccumulativeSwingIndex(buildRequest(3)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testASIWithLimitHalf() {
        IndicatorResult[] expectedResult = loadExpectedResult("accumulative_swing_index_2.json", SIResult[].class);
        SIResult[] actualResult = new AccumulativeSwingIndex(buildRequest(0.5)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ACCUMULATIVE_SWING_INDEX}, size: {0}}");
        new AccumulativeSwingIndex(SIRequest.builder()
                .originalData(new Tick[0])
                .limitMoveValue(3)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ACCUMULATIVE_SWING_INDEX}}");
        new AccumulativeSwingIndex(SIRequest.builder()
                .originalData(null)
                .limitMoveValue(3)
                .build()).getResult();
    }

    @Test
    public void limitMoveValueLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Limit move value should be more than 0 {indicator: {ACCUMULATIVE_SWING_INDEX}, shift: {-3.00}}");
        new AccumulativeSwingIndex(SIRequest.builder()
                .originalData(new Tick[100])
                .limitMoveValue(-3)
                .build()).getResult();
    }

    @Override
    protected SIRequest buildRequest() {
        return SIRequest.builder()
                .originalData(originalData)
                .build();
    }

    private IndicatorRequest buildRequest(double limitMove) {
        SIRequest request = buildRequest();
        request.setLimitMoveValue(limitMove);
        return request;
    }

}
