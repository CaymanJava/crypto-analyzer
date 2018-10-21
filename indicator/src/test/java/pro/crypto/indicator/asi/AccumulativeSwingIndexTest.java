package pro.crypto.indicator.asi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;

public class AccumulativeSwingIndexTest extends IndicatorAbstractTest {

    @Test
    public void testASIWithLimitThree() {
        IndicatorResult[] expectedResult = loadExpectedResult("accumulative_swing_index_1.json", ASIResult[].class);
        ASIResult[] actualResult = new AccumulativeSwingIndex(buildRequest(3)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testASIWithLimitHalf() {
        IndicatorResult[] expectedResult = loadExpectedResult("accumulative_swing_index_2.json", ASIResult[].class);
        ASIResult[] actualResult = new AccumulativeSwingIndex(buildRequest(0.5)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ACCUMULATIVE_SWING_INDEX}, size: {0}}");
        new AccumulativeSwingIndex(ASIRequest.builder()
                .originalData(new Tick[0])
                .limitMoveValue(3)
                .movingAveragePeriod(20)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ACCUMULATIVE_SWING_INDEX}}");
        new AccumulativeSwingIndex(ASIRequest.builder()
                .originalData(null)
                .limitMoveValue(3)
                .movingAveragePeriod(20)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void limitMoveValueLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Limit move value should be more than 0 {indicator: {ACCUMULATIVE_SWING_INDEX}, shift: {-3.00}}");
        new AccumulativeSwingIndex(ASIRequest.builder()
                .originalData(new Tick[100])
                .limitMoveValue(-3)
                .movingAveragePeriod(20)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void originalDataSizeLessThanPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ACCUMULATIVE_SWING_INDEX}, period: {20}, size: {19}}");
        new AccumulativeSwingIndex(ASIRequest.builder()
                .originalData(new Tick[19])
                .limitMoveValue(3)
                .movingAveragePeriod(20)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {ACCUMULATIVE_SWING_INDEX}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new AccumulativeSwingIndex(ASIRequest.builder()
                .originalData(new Tick[100])
                .limitMoveValue(3)
                .movingAveragePeriod(20)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .build()).getResult();
    }

    @Override
    protected ASIRequest buildRequest() {
        return ASIRequest.builder()
                .originalData(originalData)
                .movingAveragePeriod(20)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

    private IndicatorRequest buildRequest(double limitMove) {
        ASIRequest request = buildRequest();
        request.setLimitMoveValue(limitMove);
        return request;
    }

}
