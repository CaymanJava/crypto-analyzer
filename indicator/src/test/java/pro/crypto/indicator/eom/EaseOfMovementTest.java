package pro.crypto.indicator.eom;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;

public class EaseOfMovementTest extends IndicatorAbstractTest {

    @Test
    public void testEaseOfMovementWithPeriodFourteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("ease_of_movement.json", EOMResult[].class);
        EOMResult[] actualResult = new EaseOfMovement(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {EASE_OF_MOVEMENT}, size: {0}}");
        new EaseOfMovement(EOMRequest.builder()
                .originalData(new Tick[0])
                .movingAveragePeriod(14)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {EASE_OF_MOVEMENT}}");
        new EaseOfMovement(EOMRequest.builder()
                .originalData(null)
                .movingAveragePeriod(14)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void movingAveragePeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {EASE_OF_MOVEMENT}, period: {14}, size: {13}}");
        new EaseOfMovement(EOMRequest.builder()
                .originalData(new Tick[13])
                .movingAveragePeriod(14)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void movingAveragePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {EASE_OF_MOVEMENT}, period: {-14}}");
        new EaseOfMovement(EOMRequest.builder()
                .originalData(new Tick[100])
                .movingAveragePeriod(-14)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {EASE_OF_MOVEMENT}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new EaseOfMovement(EOMRequest.builder()
                .originalData(new Tick[100])
                .movingAveragePeriod(14)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return EOMRequest.builder()
                .originalData(originalData)
                .movingAveragePeriod(14)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build();
    }

}
