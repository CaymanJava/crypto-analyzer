package pro.crypto.indicator.imi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class IntradayMomentumIndexTest extends IndicatorAbstractTest {

    @Test
    public void testIntradayMomentumIndexWithPeriodTwenty() {
        IMIResult[] result = new IntradayMomentumIndex(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[10].getIndicatorValue());
        assertNull(result[18].getIndicatorValue());
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(36.3175349051));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getIndicatorValue(), toBigDecimal(40.7282363051));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(52.7627390753));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(79.9308308993));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(47.7846251606));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {INTRADAY_MOMENTUM_INDEX}, size: {0}}");
        new IntradayMomentumIndex(IMIRequest.builder()
                .originalData(new Tick[0])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {INTRADAY_MOMENTUM_INDEX}}");
        new IntradayMomentumIndex(IMIRequest.builder()
                .originalData(null)
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {INTRADAY_MOMENTUM_INDEX}, period: {20}, size: {19}}");
        new IntradayMomentumIndex(IMIRequest.builder()
                .originalData(new Tick[19])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {INTRADAY_MOMENTUM_INDEX}, period: {-20}");
        new IntradayMomentumIndex(IMIRequest.builder()
                .originalData(new Tick[100])
                .period(-20)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return IMIRequest.builder()
                .originalData(originalData)
                .period(20)
                .build();
    }

}
