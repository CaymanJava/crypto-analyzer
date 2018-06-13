package pro.crypto.indicators.hv;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.HVRequest;
import pro.crypto.model.result.HVResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class HistoricalVolatilityTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testHistoricalVolatility() {
        HVResult[] result = new HistoricalVolatility(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[5].getIndicatorValue());
        assertNull(result[9].getIndicatorValue());
        assertEquals(result[10].getTime(), of(2018, 3, 7, 0, 0));
        assertEquals(result[10].getIndicatorValue(), toBigDecimal(32.1906408905));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getIndicatorValue(), toBigDecimal(42.2651431570));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(47.2911420422));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(25.4007900361));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(27.3711549068));
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

    private HVRequest buildRequest() {
        return HVRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .period(10)
                .daysPerYear(252)
                .standardDeviations(1)
                .build();
    }

}