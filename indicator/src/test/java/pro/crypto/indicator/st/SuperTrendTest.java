package pro.crypto.indicator.st;

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

public class SuperTrendTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testSuperTrend() {
        STResult[] result = new SuperTrend(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[5].getIndicatorValue());
        assertEquals(result[6].getTime(), of(2018, 3, 3, 0, 0));
        assertEquals(result[6].getIndicatorValue(), toBigDecimal(1435.9779));
        assertEquals(result[22].getTime(), of(2018, 3, 19, 0, 0));
        assertEquals(result[22].getIndicatorValue(), toBigDecimal(1273.0254678606));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(1150.5212254002));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(1283.5129836306));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(1351.9751256747));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1471.4809896234));
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

    private IndicatorRequest buildRequest() {
        return STRequest.builder()
                .originalData(originalData)
                .period(7)
                .multiplier(3)
                .build();
    }

}