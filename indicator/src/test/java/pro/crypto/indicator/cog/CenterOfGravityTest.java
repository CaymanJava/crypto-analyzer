package pro.crypto.indicator.cog;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.MOVING_AVERAGE_CONVERGENCE_DIVERGENCE;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class CenterOfGravityTest extends IndicatorAbstractTest {

    @Test
    public void testCenterOfGravityWithPeriodsTen() {
        IndicatorResult[] expectedResult = loadExpectedResult("center_of_gravity.json", COGResult[].class);
        COGResult[] actualResult = new CenterOfGravity(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CENTER_OF_GRAVITY}, size: {0}}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(new Tick[0])
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .signalLinePeriod(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CENTER_OF_GRAVITY}}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(null)
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .signalLinePeriod(10)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CENTER_OF_GRAVITY}, period: {20}, size: {19}}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(new Tick[19])
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .signalLinePeriod(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CENTER_OF_GRAVITY}, period: {-10}}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .signalLinePeriod(10)
                .build()).getResult();
    }

    @Test
    public void signalLinePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CENTER_OF_GRAVITY}, period: {-10}}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(new Tick[100])
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .signalLinePeriod(-10)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {CENTER_OF_GRAVITY}}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(new Tick[100])
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .signalLinePeriod(10)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {CENTER_OF_GRAVITY}}, movingAverageType: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(new Tick[100])
                .period(10)
                .priceType(CLOSE)
                .movingAverageType(MOVING_AVERAGE_CONVERGENCE_DIVERGENCE)
                .signalLinePeriod(10)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return COGRequest.builder()
                .originalData(originalData)
                .period(10)
                .priceType(CLOSE)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .signalLinePeriod(10)
                .build();
    }

}
