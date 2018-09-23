package pro.crypto.indicator.roc;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RangeOfChangeTest extends IndicatorAbstractTest {

    @Test
    public void testRangeOfChangeWithPeriodFourteen() {
        ROCResult[] result = new RangeOfChange(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[8].getIndicatorValue());
        assertNull(result[13].getIndicatorValue());
        assertEquals(result[14].getTime(), of(2018, 3, 11, 0, 0));
        assertEquals(result[14].getIndicatorValue(), toBigDecimal(-6.2436621724));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-11.2136217985));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(6.0984355716));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(13.6917609257));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(5.1699199450));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-7.2259215382));
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
