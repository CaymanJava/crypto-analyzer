package pro.crypto.indicator.si;

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

public class AccumulativeSwingIndexTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testASIWithLimitThree() {
        SIResult[] result = new AccumulativeSwingIndex(buildRequest(3)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertEquals(result[1].getTime(), of(2018, 2, 26, 0, 0));
        assertEquals(result[1].getIndicatorValue(), toBigDecimal(362.3208389553));
        assertEquals(result[9].getTime(), of(2018, 3, 6, 0, 0));
        assertEquals(result[9].getIndicatorValue(), toBigDecimal(-184.0491520448));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-2454.6066564682));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(554.7757032047));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(3439.0183430777));
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getIndicatorValue(), toBigDecimal(4616.0810518231));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(2385.4352799483));
    }

    @Test
    public void testASIWithLimitHalf() {
        SIResult[] result = new AccumulativeSwingIndex(buildRequest(0.5)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertEquals(result[1].getTime(), of(2018, 2, 26, 0, 0));
        assertEquals(result[1].getIndicatorValue(), toBigDecimal(2173.9250337321));
        assertEquals(result[9].getTime(), of(2018, 3, 6, 0, 0));
        assertEquals(result[9].getIndicatorValue(), toBigDecimal(-1104.2949122684));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-14727.6399388103));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(3328.6542192267));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(20634.1100584659));
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getIndicatorValue(), toBigDecimal(27696.4863109394));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(14312.6116796907));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ACCUMULATIVE_SWING_INDEX}, size: {0}}");
        new AccumulativeSwingIndex(SIRequest.builder()
                .originalData(new Tick[0])
                .limitMoveValue(3)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ACCUMULATIVE_SWING_INDEX}}");
        new AccumulativeSwingIndex(SIRequest.builder()
                .originalData(null)
                .limitMoveValue(3)
                .build()).getResult();
    }

    @Test
    public void limitMoveValueLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Limit move value should be more than 0 {indicator: {ACCUMULATIVE_SWING_INDEX}, shift: {-3.00}}");
        new AccumulativeSwingIndex(SIRequest.builder()
                .originalData(new Tick[100])
                .limitMoveValue(-3)
                .build()).getResult();
    }

    private IndicatorRequest buildRequest(double limitMoveValue) {
        return SIRequest.builder()
                .originalData(originalData)
                .limitMoveValue(limitMoveValue)
                .build();
    }

}