package pro.crypto.indicator.ma;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.TRIPLE_EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class TripleExponentialMovingAverageTest extends IndicatorAbstractTest {

    @Test
    public void testTripleExponentialMovingAverageWithPeriodFourteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("triple_exponential_moving_average.json", MAResult[].class);
        MAResult[] actualResult = MovingAverageFactory.create(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[0])
                .period(14)
                .indicatorType(TRIPLE_EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(null)
                .period(14)
                .indicatorType(TRIPLE_EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}, period: {14}, size: {13}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[13])
                .period(14)
                .indicatorType(TRIPLE_EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void triplePeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}, period: {40}, size: {39}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[39])
                .period(14)
                .indicatorType(TRIPLE_EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}, period: {-14}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .indicatorType(TRIPLE_EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[100])
                .period(14)
                .indicatorType(TRIPLE_EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(14)
                .indicatorType(TRIPLE_EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

}
