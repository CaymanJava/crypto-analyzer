package pro.crypto.indicator.rma;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RainbowMovingAverageTest extends IndicatorAbstractTest {

    @Test
    public void testRainbowMovingAverage() {
        IndicatorResult[] expectedResult = loadExpectedResult("rainbow_moving_average.json", RMAResult[].class);
        RMAResult[] actualResult = new RainbowMovingAverage(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {RAINBOW_MOVING_AVERAGE}, size: {0}}");
        new RainbowMovingAverage(RMARequest.builder()
                .originalData(new Tick[0])
                .period(2)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {RAINBOW_MOVING_AVERAGE}}");
        new RainbowMovingAverage(RMARequest.builder()
                .originalData(null)
                .period(2)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {RAINBOW_MOVING_AVERAGE}}");
        new RainbowMovingAverage(RMARequest.builder()
                .originalData(new Tick[100])
                .period(2)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {RAINBOW_MOVING_AVERAGE}, period: {-2}}");
        new RainbowMovingAverage(RMARequest.builder()
                .originalData(new Tick[100])
                .period(-2)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void notEnoughDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RAINBOW_MOVING_AVERAGE}, period: {110}, size: {100}}");
        new RainbowMovingAverage(RMARequest.builder()
                .originalData(new Tick[100])
                .period(12)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return RMARequest.builder()
                .originalData(originalData)
                .period(2)
                .priceType(CLOSE)
                .build();
    }

}
