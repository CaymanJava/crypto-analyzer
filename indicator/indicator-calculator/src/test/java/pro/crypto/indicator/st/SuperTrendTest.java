package pro.crypto.indicator.st;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class SuperTrendTest extends IndicatorAbstractTest {

    @Test
    public void testSuperTrend() {
        IndicatorResult[] expectedResult = loadExpectedResult("super_trend.json", STResult[].class);
        STResult[] actualResult = new SuperTrend(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {SUPER_TREND}, size: {0}}");
        new SuperTrend(STRequest.builder()
                .originalData(new Tick[0])
                .period(7)
                .multiplier(3)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {SUPER_TREND}}");
        new SuperTrend(STRequest.builder()
                .originalData(null)
                .period(7)
                .multiplier(3)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {SUPER_TREND}, period: {80}, size: {79}}");
        new SuperTrend(STRequest.builder()
                .originalData(new Tick[79])
                .period(80)
                .multiplier(3)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {SUPER_TREND}, period: {-7}");
        new SuperTrend(STRequest.builder()
                .originalData(new Tick[100])
                .period(-7)
                .multiplier(3)
                .build()).getResult();
    }

    @Test
    public void multiplierLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Shift should be more or equals 0 {indicator: {SUPER_TREND}, shift: {-3.00}}");
        new SuperTrend(STRequest.builder()
                .originalData(new Tick[100])
                .period(7)
                .multiplier(-3)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return STRequest.builder()
                .originalData(originalData)
                .period(7)
                .multiplier(3)
                .build();
    }

}
