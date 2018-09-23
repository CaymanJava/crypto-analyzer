package pro.crypto.indicator.cc;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class CoppockCurveTest extends IndicatorAbstractTest {

    @Test
    public void testCoppockCurveWithDefaultParameters() {
        CCResult[] result = new CoppockCurve(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[5].getIndicatorValue());
        assertNull(result[22].getIndicatorValue());
        assertEquals(result[23].getTime(), of(2018, 3, 20, 0, 0));
        assertEquals(result[23].getIndicatorValue(), toBigDecimal(-15.2202068310));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(1.9610278079));
        assertEquals(result[34].getTime(), of(2018, 3, 31, 0, 0));
        assertEquals(result[34].getIndicatorValue(), toBigDecimal(10.5274283137));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(20.8735836322));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(9.3044077192));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-5.2733431323));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {COPPOCK_CURVE}, size: {0}}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .shortROCPeriod(11)
                .longROCPeriod(14)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {COPPOCK_CURVE}}");
        new CoppockCurve(CCRequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .shortROCPeriod(11)
                .longROCPeriod(14)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodSumMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {COPPOCK_CURVE}, period: {24}, size: {23}}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[23])
                .priceType(CLOSE)
                .shortROCPeriod(11)
                .longROCPeriod(14)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void longPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {COPPOCK_CURVE}, period: {-14}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .shortROCPeriod(11)
                .longROCPeriod(-14)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void shortPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {COPPOCK_CURVE}, period: {-11}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .shortROCPeriod(-11)
                .longROCPeriod(14)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {COPPOCK_CURVE}, period: {-10}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .shortROCPeriod(11)
                .longROCPeriod(14)
                .period(-10)
                .build()).getResult();
    }

    @Test
    public void longPeriodLessThanShortPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Short RoC Period should be less than Long Roc Period {indicator: {COPPOCK_CURVE}, conversionLinePeriod: {14}, baseLinePeriod: {11}}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .shortROCPeriod(14)
                .longROCPeriod(11)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {COPPOCK_CURVE}}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[100])
                .shortROCPeriod(11)
                .longROCPeriod(14)
                .period(10)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return CCRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .shortROCPeriod(11)
                .longROCPeriod(14)
                .period(10)
                .build();
    }

}
