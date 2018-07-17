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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class SwingIndexTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testSwingIndexWithLimitThree() {
        SIResult[] result = new SwingIndex(buildRequest(3)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertEquals(result[1].getTime(), of(2018, 2, 26, 0, 0));
        assertEquals(result[1].getIndicatorValue(), toBigDecimal(362.3208389553));
        assertEquals(result[9].getTime(), of(2018, 3, 6, 0, 0));
        assertEquals(result[9].getIndicatorValue(), toBigDecimal(-218.4040399132));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-650.3138934024));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(870.6041160592));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(152.9630983823));
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getIndicatorValue(), toBigDecimal(-891.1309483551));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-961.2723880502));
    }

    @Test
    public void testSwingIndexWithLimitHalf() {
        SIResult[] result = new SwingIndex(buildRequest(0.5)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertEquals(result[1].getTime(), of(2018, 2, 26, 0, 0));
        assertEquals(result[1].getIndicatorValue(), toBigDecimal(2173.9250337321));
        assertEquals(result[9].getTime(), of(2018, 3, 6, 0, 0));
        assertEquals(result[9].getIndicatorValue(), toBigDecimal(-1310.4242394793));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-3901.8833604146));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(5223.6246963553));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(917.7785902936));
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getIndicatorValue(), toBigDecimal(-5346.7856901306));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-5767.6343283011));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {SWING_INDEX}, size: {0}}");
        new SwingIndex(SIRequest.builder()
                .originalData(new Tick[0])
                .limitMoveValue(3)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {SWING_INDEX}}");
        new SwingIndex(SIRequest.builder()
                .originalData(null)
                .limitMoveValue(3)
                .build()).getResult();
    }

    @Test
    public void limitMoveValueLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Limit move value should be more than 0 {indicator: {SWING_INDEX}, shift: {-3.00}}");
        new SwingIndex(SIRequest.builder()
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