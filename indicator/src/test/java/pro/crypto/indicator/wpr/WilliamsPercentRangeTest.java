package pro.crypto.indicator.wpr;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class WilliamsPercentRangeTest extends IndicatorAbstractTest {

    @Test
    public void testWPRWithDefaultPeriod() {
        WPRResult[] result = new WilliamsPercentRange(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[6].getIndicatorValue());
        assertNull(result[12].getIndicatorValue());
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getIndicatorValue(), toBigDecimal(-96.2617514));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getIndicatorValue(), toBigDecimal(-22.22984824));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(-6.25));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(-11.32720372));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-99.96859261));
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
