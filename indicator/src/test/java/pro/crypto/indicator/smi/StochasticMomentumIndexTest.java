package pro.crypto.indicator.smi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;

public class StochasticMomentumIndexTest extends IndicatorAbstractTest {

    @Test
    public void testStochasticMomentumIndexWithTenAndThreePeriods() {
        IndicatorResult[] expectedResult = loadExpectedResult("stochastic_momentum_index_1.json", SMIResult[].class);
        SMIResult[] actualResult = new StochasticMomentumIndex(buildRequest(10, 3)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testStochasticMomentumIndexWithFourteenAndFourPeriods() {
        IndicatorResult[] expectedResult = loadExpectedResult("stochastic_momentum_index_2.json", SMIResult[].class);
        SMIResult[] actualResult = new StochasticMomentumIndex(buildRequest(14, 4)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {STOCHASTIC_MOMENTUM_INDEX}, size: {0}}");
        new StochasticMomentumIndex(SMIRequest.builder()
                .originalData(new Tick[0])
                .period(10)
                .smoothingPeriod(3)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {STOCHASTIC_MOMENTUM_INDEX}}");
        new StochasticMomentumIndex(SMIRequest.builder()
                .originalData(null)
                .period(10)
                .smoothingPeriod(3)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {STOCHASTIC_MOMENTUM_INDEX}, period: {17}, size: {16}}");
        new StochasticMomentumIndex(SMIRequest.builder()
                .originalData(new Tick[16])
                .period(10)
                .smoothingPeriod(3)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {STOCHASTIC_MOMENTUM_INDEX}, period: {-10}");
        new StochasticMomentumIndex(SMIRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .smoothingPeriod(3)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void smoothingPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {STOCHASTIC_MOMENTUM_INDEX}, period: {-3}");
        new StochasticMomentumIndex(SMIRequest.builder()
                .originalData(new Tick[100])
                .period(10)
                .smoothingPeriod(-3)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {STOCHASTIC_MOMENTUM_INDEX}}, movingAverageType: {AVERAGE_TRUE_RANGE}");
        new StochasticMomentumIndex(SMIRequest.builder()
                .originalData(new Tick[100])
                .period(10)
                .smoothingPeriod(3)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .build()).getResult();
    }

    @Override
    protected SMIRequest buildRequest() {
        return SMIRequest.builder()
                .originalData(originalData)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

    private IndicatorRequest buildRequest(int period, int smoothingPeriod) {
        SMIRequest request = buildRequest();
        request.setPeriod(period);
        request.setSmoothingPeriod(smoothingPeriod);
        return request;
    }

}
