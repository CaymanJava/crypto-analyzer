package pro.crypto.indicator.hv;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class HistoricalVolatilityTest extends IndicatorAbstractTest {

    @Test
    public void testHistoricalVolatility() {
        IndicatorResult[] expectedResult = loadExpectedResult("historical_volatility.json", HVResult[].class);
        HVResult[] actualResult = new HistoricalVolatility(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {HISTORICAL_VOLATILITY}, size: {0}}");
        new HistoricalVolatility(HVRequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .period(10)
                .daysPerYear(252)
                .standardDeviations(1)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {HISTORICAL_VOLATILITY}}");
        new HistoricalVolatility(HVRequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .period(10)
                .daysPerYear(252)
                .standardDeviations(1)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {HISTORICAL_VOLATILITY}, period: {21}, size: {20}}");
        new HistoricalVolatility(HVRequest.builder()
                .originalData(new Tick[20])
                .priceType(CLOSE)
                .period(20)
                .daysPerYear(252)
                .standardDeviations(1)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {HISTORICAL_VOLATILITY}, period: {-10}}");
        new HistoricalVolatility(HVRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(-10)
                .daysPerYear(252)
                .standardDeviations(1)
                .build()).getResult();
    }

    @Test
    public void standardDeviationsLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {HISTORICAL_VOLATILITY}, period: {-1}}");
        new HistoricalVolatility(HVRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(10)
                .daysPerYear(252)
                .standardDeviations(-1)
                .build()).getResult();
    }

    @Test
    public void daysPerYearLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {HISTORICAL_VOLATILITY}, period: {-252}}");
        new HistoricalVolatility(HVRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(10)
                .daysPerYear(-252)
                .standardDeviations(1)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {HISTORICAL_VOLATILITY}}");
        new HistoricalVolatility(HVRequest.builder()
                .originalData(new Tick[100])
                .period(10)
                .daysPerYear(252)
                .standardDeviations(1)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return HVRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .period(10)
                .daysPerYear(252)
                .standardDeviations(1)
                .build();
    }

}
