package pro.crypto.indicator.cmf;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class ChaikinMoneyFlowTest extends IndicatorAbstractTest {

    @Test
    public void testCMFTwentyOneDaysPeriod() {
        CMFResult[] result = new ChaikinMoneyFlow(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[10].getIndicatorValue());
        assertNull(result[19].getIndicatorValue());
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertEquals(result[20].getIndicatorValue(), toBigDecimal(-0.100887968));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(-0.1569850648));
        assertEquals(result[34].getTime(), of(2018, 3, 31, 0, 0));
        assertEquals(result[34].getIndicatorValue(), toBigDecimal(0.0208990395));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(0.2899809672));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(0.1165375399));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CHAIKIN_MONEY_FLOW}, size: {0}}");
        new ChaikinMoneyFlow(CMFRequest.builder()
                .originalData(new Tick[0])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CHAIKIN_MONEY_FLOW}}");
        new ChaikinMoneyFlow(CMFRequest.builder()
                .originalData(null)
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CHAIKIN_MONEY_FLOW}, period: {20}, size: {19}}");
        new ChaikinMoneyFlow(CMFRequest.builder()
                .originalData(new Tick[19])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHAIKIN_MONEY_FLOW}, period: {-20}}");
        new ChaikinMoneyFlow(CMFRequest.builder()
                .originalData(new Tick[19])
                .period(-20)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return CMFRequest.builder()
                .originalData(originalData)
                .period(21)
                .build();
    }

}
