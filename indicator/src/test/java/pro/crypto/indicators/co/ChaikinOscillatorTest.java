package pro.crypto.indicators.co;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.CORequest;
import pro.crypto.model.result.COResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class ChaikinOscillatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testChaikinOscillatorWithPeriodsThreeAndTen() {
        COResult[] result = new ChaikinOscillator(buildCORequest(3, 10)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[8].getIndicatorValue());
        assertEquals(result[9].getTime(), of(2018, 3, 6, 0, 0));
        assertEquals(result[9].getIndicatorValue(), toBigDecimal(-65.1840313725));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-54.4000199895));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(21.9548098196));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(122.1798438436));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-15.7606795517));
    }

    @Test
    public void testChaikinOscillatorWithPeriodsSixAndTwenty() {
        COResult[] result = new ChaikinOscillator(buildCORequest(6, 20)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[18].getIndicatorValue());
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-137.323594815));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(-83.0525354663));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(129.0547908907));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(28.4716151962));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CHAIKIN_OSCILLATOR}, size: {0}}");
        new ChaikinOscillator(CORequest.builder()
                .originalData(new Tick[0])
                .slowPeriod(3)
                .fastPeriod(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CHAIKIN_OSCILLATOR}}");
        new ChaikinOscillator(CORequest.builder()
                .originalData(null)
                .slowPeriod(3)
                .fastPeriod(10)
                .build()).getResult();
    }

    @Test
    public void emptySlowPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHAIKIN_OSCILLATOR}, period: {0}}");
        new ChaikinOscillator(CORequest.builder()
                .originalData(new Tick[30])
                .fastPeriod(10)
                .build()).getResult();
    }

    @Test
    public void emptyFastPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHAIKIN_OSCILLATOR}, period: {0}}");
        new ChaikinOscillator(CORequest.builder()
                .originalData(new Tick[30])
                .slowPeriod(10)
                .build()).getResult();
    }

    @Test
    public void notEnoughDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CHAIKIN_OSCILLATOR}, period: {10}, size: {5}}");
        new ChaikinOscillator(CORequest.builder()
                .originalData(new Tick[5])
                .slowPeriod(3)
                .fastPeriod(10)
                .build()).getResult();
    }

    private CORequest buildCORequest(int slowPeriod, int fastPeriod) {
        return CORequest.builder()
                .originalData(originalData)
                .slowPeriod(slowPeriod)
                .fastPeriod(fastPeriod)
                .build();
    }

}