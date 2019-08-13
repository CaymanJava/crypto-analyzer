package pro.crypto.indicator.rv;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RelativeVolatilityTest extends IndicatorAbstractTest {

    @Test
    public void testRelativeVolatility() {
        IndicatorResult[] expectedResult = loadExpectedResult("relative_volatility_index.json", RVResult[].class);
        RVResult[] actualResult = new RelativeVolatility(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {RELATIVE_VOLATILITY}, size: {0}}");
        new RelativeVolatility(RVRequest.builder()
                .originalData(new Tick[0])
                .period(14)
                .stDevPeriod(10)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {RELATIVE_VOLATILITY}}");
        new RelativeVolatility(RVRequest.builder()
                .originalData(null)
                .period(14)
                .stDevPeriod(10)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RELATIVE_VOLATILITY}, period: {24}, size: {23}}");
        new RelativeVolatility(RVRequest.builder()
                .originalData(new Tick[23])
                .period(14)
                .stDevPeriod(10)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {RELATIVE_VOLATILITY}, period: {-14}");
        new RelativeVolatility(RVRequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .stDevPeriod(10)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void stDevPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {RELATIVE_VOLATILITY}, period: {-10}");
        new RelativeVolatility(RVRequest.builder()
                .originalData(new Tick[100])
                .period(14)
                .stDevPeriod(-10)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {RELATIVE_VOLATILITY}}");
        new RelativeVolatility(RVRequest.builder()
                .originalData(new Tick[100])
                .period(14)
                .stDevPeriod(10)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return RVRequest.builder()
                .originalData(originalData)
                .period(14)
                .stDevPeriod(10)
                .priceType(CLOSE)
                .build();
    }

}
