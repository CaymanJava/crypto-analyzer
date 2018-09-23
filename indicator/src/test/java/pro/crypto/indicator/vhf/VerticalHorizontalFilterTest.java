package pro.crypto.indicator.vhf;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class VerticalHorizontalFilterTest extends IndicatorAbstractTest {

    @Test
    public void testVerticalHorizontalFilterWithDefaultPeriod() {
        VHFResult[] result = new VerticalHorizontalFilter(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[26].getIndicatorValue());
        assertEquals(result[27].getTime(), of(2018, 3, 24, 0, 0));
        assertEquals(result[27].getIndicatorValue(), toBigDecimal(0.3376665956));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(0.3028674035));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(0.4025610201));
        assertEquals(result[61].getTime(), of(2018, 4, 27, 0, 0));
        assertEquals(result[61].getIndicatorValue(), toBigDecimal(0.2934743948));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(0.2552686154));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {VERTICAL_HORIZONTAL_FILTER}, size: {0}}");
        new VerticalHorizontalFilter(VHFRequest.builder()
                .originalData(new Tick[0])
                .period(28)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {VERTICAL_HORIZONTAL_FILTER}}");
        new VerticalHorizontalFilter(VHFRequest.builder()
                .originalData(null)
                .period(28)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {VERTICAL_HORIZONTAL_FILTER}, period: {28}, size: {27}}");
        new VerticalHorizontalFilter(VHFRequest.builder()
                .originalData(new Tick[27])
                .period(28)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {VERTICAL_HORIZONTAL_FILTER}, period: {-28}}");
        new VerticalHorizontalFilter(VHFRequest.builder()
                .originalData(new Tick[100])
                .period(-28)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return VHFRequest.builder()
                .originalData(originalData)
                .period(28)
                .build();
    }

}
