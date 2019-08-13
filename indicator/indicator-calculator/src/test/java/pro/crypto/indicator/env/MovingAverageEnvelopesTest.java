package pro.crypto.indicator.env;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;

public class MovingAverageEnvelopesTest extends IndicatorAbstractTest {

    @Test
    public void testMovingAverageEnvelopesWithPercentageFive() {
        IndicatorResult[] expectedResult = loadExpectedResult("moving_average_envelopes_1.json", ENVResult[].class);
        ENVResult[] actualResult = new MovingAverageEnvelopes(buildRequestWithPercentageFive()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testMovingAverageEnvelopesWithPercentageSeven() {
        IndicatorResult[] expectedResult = loadExpectedResult("moving_average_envelopes_2.json", ENVResult[].class);
        ENVResult[] actualResult = new MovingAverageEnvelopes(buildRequestWithPercentageSeven()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {MOVING_AVERAGE_ENVELOPES}, size: {0}}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(new Tick[0])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .indentationPercentage(5)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {MOVING_AVERAGE_ENVELOPES}}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(null)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .indentationPercentage(5)
                .build()).getResult();
    }

    @Test
    public void movingAveragePeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {MOVING_AVERAGE_ENVELOPES}, period: {20}, size: {19}}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(new Tick[19])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .indentationPercentage(5)
                .build()).getResult();
    }

    @Test
    public void movingAveragePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {MOVING_AVERAGE_ENVELOPES}, period: {-20}}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(-20)
                .indentationPercentage(5)
                .build()).getResult();
    }

    @Test
    public void percentageMoreOrEqualsThanHundredTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Percentage should be in the range (0, 100) {indicator: {MOVING_AVERAGE_ENVELOPES}, indentationPercentage: {100,00}}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .indentationPercentage(100)
                .build()).getResult();
    }

    @Test
    public void percentageLessOrEqualsThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Percentage should be in the range (0, 100) {indicator: {MOVING_AVERAGE_ENVELOPES}, indentationPercentage: {0,00}}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .indentationPercentage(0)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {MOVING_AVERAGE_ENVELOPES}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .movingAveragePeriod(20)
                .indentationPercentage(5)
                .build()).getResult();
    }

    @Override
    protected ENVRequest buildRequest() {
        return ENVRequest.builder()
                .originalData(originalData)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .build();
    }

    private ENVRequest buildRequestWithPercentageFive() {
        ENVRequest request = buildRequest();
        request.setIndentationPercentage(5);
        return request;
    }

    private ENVRequest buildRequestWithPercentageSeven() {
        ENVRequest request = buildRequest();
        request.setIndentationPercentage(7);
        return request;
    }

}
