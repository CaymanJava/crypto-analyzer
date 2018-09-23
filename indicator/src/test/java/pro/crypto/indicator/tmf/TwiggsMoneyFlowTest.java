package pro.crypto.indicator.tmf;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class TwiggsMoneyFlowTest extends IndicatorAbstractTest {

    @Test
    public void testTwiggsMoneyFlowWithPeriodTwentyOne() {
        TMFResult[] result = new TwiggsMoneyFlow(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineValue());
        assertNull(result[20].getIndicatorValue());
        assertNull(result[20].getSignalLineValue());
        assertEquals(result[21].getTime(), of(2018, 3, 18, 0, 0));
        assertEquals(result[21].getIndicatorValue(), toBigDecimal(-14.6717369334));
        assertNull(result[21].getSignalLineValue());
        assertEquals(result[41].getTime(), of(2018, 4, 7, 0, 0));
        assertEquals(result[41].getIndicatorValue(), toBigDecimal(10.5814093552));
        assertEquals(result[41].getSignalLineValue(), toBigDecimal(-5.4752419188));
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getIndicatorValue(), toBigDecimal(12.4684050351));
        assertEquals(result[49].getSignalLineValue(), toBigDecimal(10.3131862664));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(33.575577084));
        assertEquals(result[58].getSignalLineValue(), toBigDecimal(16.8028720873));
        assertEquals(result[67].getTime(), of(2018, 5, 3, 0, 0));
        assertEquals(result[67].getIndicatorValue(), toBigDecimal(5.5230219736));
        assertEquals(result[67].getSignalLineValue(), toBigDecimal(16.6354731831));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(4.0645185282));
        assertEquals(result[72].getSignalLineValue(), toBigDecimal(11.9775552181));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {TWIGGS_MONEY_FLOW}, size: {0}}");
        new TwiggsMoneyFlow(TMFRequest.builder()
                .originalData(new Tick[0])
                .period(21)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {TWIGGS_MONEY_FLOW}}");
        new TwiggsMoneyFlow(TMFRequest.builder()
                .originalData(null)
                .period(21)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {TWIGGS_MONEY_FLOW}, period: {21}, size: {20}}");
        new TwiggsMoneyFlow(TMFRequest.builder()
                .originalData(new Tick[20])
                .period(21)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {TWIGGS_MONEY_FLOW}, period: {-21}}");
        new TwiggsMoneyFlow(TMFRequest.builder()
                .originalData(new Tick[100])
                .period(-21)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return TMFRequest.builder()
                .originalData(originalData)
                .period(21)
                .build();
    }

}
