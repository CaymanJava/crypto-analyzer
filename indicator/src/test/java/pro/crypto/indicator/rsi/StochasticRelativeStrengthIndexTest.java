package pro.crypto.indicator.rsi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.*;

public class StochasticRelativeStrengthIndexTest extends IndicatorAbstractTest {

    @Test
    public void testStochasticRelativeStrengthIndexWithExponentialMovingAverage() {
        IndicatorResult[] expectedResult = loadExpectedResult("stochastic_relative_strength_index.json", RSIResult[].class);
        RSIResult[] actualResult = new StochasticRelativeStrengthIndex(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {STOCHASTIC_RELATIVE_STRENGTH_INDEX}, size: {0}}");
        new StochasticRelativeStrengthIndex(StochRSIRequest.builder()
                .originalData(new Tick[0])
                .movingAverageType(SMOOTHED_MOVING_AVERAGE)
                .rsiPeriod(14)
                .stochPeriod(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {STOCHASTIC_RELATIVE_STRENGTH_INDEX}}");
        new StochasticRelativeStrengthIndex(StochRSIRequest.builder()
                .originalData(null)
                .movingAverageType(SMOOTHED_MOVING_AVERAGE)
                .rsiPeriod(14)
                .stochPeriod(14)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {STOCHASTIC_RELATIVE_STRENGTH_INDEX}, period: {34}, size: {33}}");
        new StochasticRelativeStrengthIndex(StochRSIRequest.builder()
                .originalData(new Tick[33])
                .movingAverageType(SMOOTHED_MOVING_AVERAGE)
                .rsiPeriod(20)
                .stochPeriod(14)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {STOCHASTIC_RELATIVE_STRENGTH_INDEX}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new StochasticRelativeStrengthIndex(StochRSIRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .rsiPeriod(14)
                .stochPeriod(14)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return StochRSIRequest.builder()
                .originalData(originalData)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .rsiPeriod(14)
                .stochPeriod(14)
                .build();
    }

}
