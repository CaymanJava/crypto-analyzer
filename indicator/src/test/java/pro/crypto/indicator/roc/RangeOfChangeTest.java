package pro.crypto.indicator.roc;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RangeOfChangeTest extends IndicatorAbstractTest {

    @Test
    public void testRangeOfChangeWithPeriodFourteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("range_of_change.json", ROCResult[].class);
        ROCResult[] actualResult = new RangeOfChange(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {RANGE_OF_CHANGE}, size: {0}}");
        new RangeOfChange(ROCRequest.builder()
                .originalData(new Tick[0])
                .period(14)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {RANGE_OF_CHANGE}}");
        new RangeOfChange(ROCRequest.builder()
                .originalData(null)
                .period(14)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RANGE_OF_CHANGE}, period: {20}, size: {19}}");
        new RangeOfChange(ROCRequest.builder()
                .originalData(new Tick[19])
                .period(20)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {RANGE_OF_CHANGE}, period: {-14}");
        new RangeOfChange(ROCRequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {RANGE_OF_CHANGE}}");
        new RangeOfChange(ROCRequest.builder()
                .originalData(new Tick[100])
                .period(14)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return ROCRequest.builder()
                .originalData(originalData)
                .period(14)
                .priceType(CLOSE)
                .build();
    }

}
