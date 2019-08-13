package pro.crypto.indicator.macd;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.MOVING_AVERAGE_CONVERGENCE_DIVERGENCE;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class MovingAverageConvergenceDivergenceTest extends IndicatorAbstractTest {

    @Test
    public void testMovingAverageConvergenceDivergence() {
        IndicatorResult[] expectedResult = loadExpectedResult("moving_average_convergence_divergence.json", MACDResult[].class);
        MACDResult[] actualResult = new MovingAverageConvergenceDivergence(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, size: {0}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[0])
                .fastPeriod(12)
                .slowPeriod(26)
                .signalPeriod(9)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(null)
                .fastPeriod(12)
                .slowPeriod(26)
                .signalPeriod(9)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(12)
                .slowPeriod(26)
                .signalPeriod(9)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptySlowPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, period: {0}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptyFastPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, period: {0}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(12)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptySignalPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, period: {0}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(12)
                .slowPeriod(26)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}}, movingAverageType: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(12)
                .slowPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(MOVING_AVERAGE_CONVERGENCE_DIVERGENCE)
                .build()).getResult();
    }

    @Test
    public void notEnoughDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, period: {35}, size: {10}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[10])
                .fastPeriod(12)
                .slowPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return MACDRequest.builder()
                .originalData(originalData)
                .fastPeriod(12)
                .slowPeriod(26)
                .signalPeriod(9)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

}
