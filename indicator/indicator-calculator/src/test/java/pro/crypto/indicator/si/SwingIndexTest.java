package pro.crypto.indicator.si;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class SwingIndexTest extends IndicatorAbstractTest {

    @Test
    public void testSwingIndexWithLimitThree() {
        IndicatorResult[] expectedResult = loadExpectedResult("swing_index_1.json", SIResult[].class);
        SIResult[] actualResult = new SwingIndex(buildRequest(3)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testSwingIndexWithLimitHalf() {
        IndicatorResult[] expectedResult = loadExpectedResult("swing_index_2.json", SIResult[].class);
        SIResult[] actualResult = new SwingIndex(buildRequest(0.5)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {SWING_INDEX}, size: {0}}");
        new SwingIndex(SIRequest.builder()
                .originalData(new Tick[0])
                .limitMoveValue(3)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {SWING_INDEX}}");
        new SwingIndex(SIRequest.builder()
                .originalData(null)
                .limitMoveValue(3)
                .build()).getResult();
    }

    @Test
    public void limitMoveValueLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Limit move value should be more than 0 {indicator: {SWING_INDEX}, shift: {-3.00}}");
        new SwingIndex(SIRequest.builder()
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
