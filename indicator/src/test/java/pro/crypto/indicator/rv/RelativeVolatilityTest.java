package pro.crypto.indicator.rv;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RelativeVolatilityTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testRelativeVolatility() {
        RVResult[] result = new RelativeVolatility(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[21].getIndicatorValue());
        assertEquals(result[22].getTime(), of(2018, 3, 19, 0, 0));
        assertEquals(result[22].getIndicatorValue(), toBigDecimal(23.2611471622));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(60.5969080279));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(71.5569912959));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(65.5711960860));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(56.4805887211));
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

    private IndicatorRequest buildRequest() {
        return RVRequest.builder()
                .originalData(originalData)
                .period(14)
                .stDevPeriod(10)
                .priceType(CLOSE)
                .build();
    }

}