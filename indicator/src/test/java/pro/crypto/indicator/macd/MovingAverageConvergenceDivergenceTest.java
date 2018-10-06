package pro.crypto.indicator.macd;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.*;
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
                .slowPeriod(12)
                .fastPeriod(26)
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
                .slowPeriod(12)
                .fastPeriod(26)
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
                .slowPeriod(12)
                .fastPeriod(26)
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
                .slowPeriod(12)
                .fastPeriod(26)
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
                .slowPeriod(12)
                .fastPeriod(26)
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
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return MACDRequest.builder()
                .originalData(originalData)
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

}
