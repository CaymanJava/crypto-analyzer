package pro.crypto.indicator.adx;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class AverageDirectionalMovementIndexTest extends IndicatorAbstractTest {

    @Test
    public void testADXWithPeriodFourteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("average_directional_movement_index.json", ADXResult[].class);
        ADXResult[] actualResult = new AverageDirectionalMovementIndex(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {AVERAGE_DIRECTIONAL_MOVEMENT_INDEX}, size: {0}}");
        new AverageDirectionalMovementIndex(ADXRequest.builder()
                .originalData(new Tick[0])
                .period(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {AVERAGE_DIRECTIONAL_MOVEMENT_INDEX}}");
        new AverageDirectionalMovementIndex(ADXRequest.builder()
                .originalData(null)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {AVERAGE_DIRECTIONAL_MOVEMENT_INDEX}, period: {20}, size: {19}}");
        new AverageDirectionalMovementIndex(ADXRequest.builder()
                .originalData(new Tick[19])
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {AVERAGE_DIRECTIONAL_MOVEMENT_INDEX}, period: {-10}}");
        new AverageDirectionalMovementIndex(ADXRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return ADXRequest.builder()
                .originalData(originalData)
                .period(14)
                .build();
    }

}
