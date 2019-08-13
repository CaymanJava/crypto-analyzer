package pro.crypto.indicator.wpr;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class WilliamsPercentRangeTest extends IndicatorAbstractTest {

    @Test
    public void testWPRWithDefaultPeriod() {
        IndicatorResult[] expectedResult = loadExpectedResult("williams_percent_range.json", WPRResult[].class);
        WPRResult[] actualResult = new WilliamsPercentRange(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {WILLIAMS_PERCENT_RANGE}, size: {0}}");
        new WilliamsPercentRange(WPRRequest.builder()
                .originalData(new Tick[0])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {WILLIAMS_PERCENT_RANGE}}");
        new WilliamsPercentRange(WPRRequest.builder()
                .originalData(null)
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {WILLIAMS_PERCENT_RANGE}, period: {20}, size: {19}}");
        new WilliamsPercentRange(WPRRequest.builder()
                .originalData(new Tick[19])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {WILLIAMS_PERCENT_RANGE}, period: {-14}}");
        new WilliamsPercentRange(WPRRequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return WPRRequest.builder()
                .originalData(originalData)
                .period(14)
                .build();
    }

}
