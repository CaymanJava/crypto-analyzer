package pro.crypto.indicator.rsi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.SMOOTHED_MOVING_AVERAGE;

public class RelativeStrengthIndexTest extends IndicatorAbstractTest {

    @Test
    public void testRelativeStrengthIndexWithSmoothedMovingAverage() {
        IndicatorResult[] expectedResult = loadExpectedResult("relative_strength_index_1.json", RSIResult[].class);
        RSIResult[] actualResult = new RelativeStrengthIndex(buildRequest(SMOOTHED_MOVING_AVERAGE)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testRelativeStrengthIndexWithExponentialMovingAverage() {
        IndicatorResult[] expectedResult = loadExpectedResult("relative_strength_index_2.json", RSIResult[].class);
        RSIResult[] actualResult = new RelativeStrengthIndex(buildRequest(EXPONENTIAL_MOVING_AVERAGE)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {RELATIVE_STRENGTH_INDEX}, size: {0}}");
        new RelativeStrengthIndex(RSIRequest.builder()
                .originalData(new Tick[0])
                .movingAverageType(SMOOTHED_MOVING_AVERAGE)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {RELATIVE_STRENGTH_INDEX}}");
        new RelativeStrengthIndex(RSIRequest.builder()
                .originalData(null)
                .movingAverageType(SMOOTHED_MOVING_AVERAGE)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RELATIVE_STRENGTH_INDEX}, period: {20}, size: {19}}");
        new RelativeStrengthIndex(RSIRequest.builder()
                .originalData(new Tick[19])
                .movingAverageType(SMOOTHED_MOVING_AVERAGE)
                .period(20)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {RELATIVE_STRENGTH_INDEX}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new RelativeStrengthIndex(RSIRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .period(14)
                .build()).getResult();
    }

    @Override
    protected RSIRequest buildRequest() {
        return RSIRequest.builder()
                .originalData(originalData)
                .period(14)
                .build();
    }

    private IndicatorRequest buildRequest(IndicatorType movingAverageType) {
        RSIRequest request = buildRequest();
        request.setMovingAverageType(movingAverageType);
        return request;
    }

}
