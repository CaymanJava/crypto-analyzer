package pro.crypto.indicator.atr;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;

public class AverageTrueRangeTest extends IndicatorAbstractTest {

    @Test
    public void testATRWithPeriodTen() {
        IndicatorResult[] expectedResult = loadExpectedResult("average_true_range.json", ATRResult[].class);
        ATRResult[] actualResult = new AverageTrueRange(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {AVERAGE_TRUE_RANGE}, size: {0}}");
        new AverageTrueRange(ATRRequest.builder()
                .originalData(new Tick[0])
                .period(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {AVERAGE_TRUE_RANGE}}");
        new AverageTrueRange(ATRRequest.builder()
                .originalData(null)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {AVERAGE_TRUE_RANGE}, period: {20}, size: {19}}");
        new AverageTrueRange(ATRRequest.builder()
                .originalData(new Tick[19])
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {AVERAGE_TRUE_RANGE}, period: {-10}}");
        new AverageTrueRange(ATRRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(10)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(10)
                .build();
    }

}
