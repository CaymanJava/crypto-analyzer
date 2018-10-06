package pro.crypto.indicator.stdev;

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

public class StandardDeviationTest extends IndicatorAbstractTest {

    @Test
    public void testStDevWithDefaultParamsAndPeriodFourteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("standard_deviation.json", StDevResult[].class);
        StDevResult[] actualResult = new StandardDeviation(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {STANDARD_DEVIATION}, size: {0}}");
        new StandardDeviation(StDevRequest.builder()
                .originalData(new Tick[0])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {STANDARD_DEVIATION}}");
        new StandardDeviation(StDevRequest.builder()
                .originalData(null)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {STANDARD_DEVIATION}, period: {20}, size: {10}}");
        new StandardDeviation(StDevRequest.builder()
                .originalData(new Tick[10])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .period(20)
                .build()).getResult();
    }

    @Test
    public void shortPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {STANDARD_DEVIATION}, period: {-20}}");
        new StandardDeviation(StDevRequest.builder()
                .originalData(new Tick[10])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .period(-20)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {STANDARD_DEVIATION}}");
        new StandardDeviation(StDevRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {STANDARD_DEVIATION}}, movingAverageType: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}");
        new StandardDeviation(StDevRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(MOVING_AVERAGE_CONVERGENCE_DIVERGENCE)
                .priceType(CLOSE)
                .period(14)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return StDevRequest.builder()
                .originalData(originalData)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .period(14)
                .build();
    }

}
