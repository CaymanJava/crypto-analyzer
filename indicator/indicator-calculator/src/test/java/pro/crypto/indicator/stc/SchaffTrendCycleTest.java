package pro.crypto.indicator.stc;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IncreasedQuantityIndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.MOVING_AVERAGE_CONVERGENCE_DIVERGENCE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class SchaffTrendCycleTest extends IncreasedQuantityIndicatorAbstractTest {

    @Test
    public void testSchaffTrendCycleWithDefaultParams() {
        IndicatorResult[] expectedResult = loadExpectedResult("schaff_trend_cycle.json", STCResult[].class);
        STCResult[] actualResult = new SchaffTrendCycle(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {SCHAFF_TREND_CYCLE}, size: {0}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(23)
                .longCycle(50)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {SCHAFF_TREND_CYCLE}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(23)
                .longCycle(50)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {SCHAFF_TREND_CYCLE}, period: {70}, size: {69}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[69])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(23)
                .longCycle(50)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {SCHAFF_TREND_CYCLE}, period: {-10}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(-10)
                .shortCycle(23)
                .longCycle(50)
                .build()).getResult();
    }

    @Test
    public void shortCycleLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {SCHAFF_TREND_CYCLE}, period: {-23}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(-23)
                .longCycle(50)
                .build()).getResult();
    }

    @Test
    public void longCycleLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {SCHAFF_TREND_CYCLE}, period: {-50}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(23)
                .longCycle(-50)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {SCHAFF_TREND_CYCLE}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(23)
                .longCycle(50)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {SCHAFF_TREND_CYCLE}}," +
                " movingAverageType: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(MOVING_AVERAGE_CONVERGENCE_DIVERGENCE)
                .period(10)
                .shortCycle(23)
                .longCycle(50)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return STCRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(23)
                .longCycle(50)
                .build();
    }

}
