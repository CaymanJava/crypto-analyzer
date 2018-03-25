package pro.crypto.indicators.co;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.COCreationRequest;
import pro.crypto.model.result.COResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertEquals(result[8].getTime(), of(2018, 3, 5, 0, 0));
        assertTrue(isNull(result[8].getIndicatorValue()));
        assertEquals(result[9].getTime(), of(2018, 3, 6, 0, 0));
        assertEquals(result[9].getIndicatorValue(), new BigDecimal(-65.1840313725).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), new BigDecimal(-54.4000199895).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), new BigDecimal(21.9548098196).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), new BigDecimal(122.1798438436).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), new BigDecimal(-15.7606795517).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void testChaikinOscillatorWithPeriodsSixAndTwenty() {
        COResult[] result = new ChaikinOscillator(buildCORequest(6, 20)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertEquals(result[18].getTime(), of(2018, 3, 15, 0, 0));
        assertTrue(isNull(result[18].getIndicatorValue()));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), new BigDecimal(-137.3235948150).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), new BigDecimal(-83.0525354663).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), new BigDecimal(129.0547908907).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), new BigDecimal(28.4716151962).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CHAIKIN_OSCILLATOR}, size: {0}}");
        new ChaikinOscillator(COCreationRequest.builder()
                .originalData(new Tick[0])
                .slowPeriod(3)
                .fastPeriod(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CHAIKIN_OSCILLATOR}}");
        new ChaikinOscillator(COCreationRequest.builder()
                .originalData(null)
                .slowPeriod(3)
                .fastPeriod(10)
                .build()).getResult();
    }

    @Test
    public void emptySlowPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHAIKIN_OSCILLATOR}, period: {0}}");
        new ChaikinOscillator(COCreationRequest.builder()
                .originalData(new Tick[30])
                .fastPeriod(10)
                .build()).getResult();
    }

    @Test
    public void emptyFastPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHAIKIN_OSCILLATOR}, period: {0}}");
        new ChaikinOscillator(COCreationRequest.builder()
                .originalData(new Tick[30])
                .fastPeriod(10)
                .build()).getResult();
    }

    @Test
    public void notEnoughDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is not enough {indicator: {CHAIKIN_OSCILLATOR}, tickLength: {5}, fastPeriod: {10}}");
        new ChaikinOscillator(COCreationRequest.builder()
                .originalData(new Tick[5])
                .slowPeriod(3)
                .fastPeriod(10)
                .build()).getResult();
    }

    private COCreationRequest buildCORequest(int slowPeriod, int fastPeriod) {
        return COCreationRequest.builder()
                .originalData(originalData)
                .slowPeriod(slowPeriod)
                .fastPeriod(fastPeriod)
                .build();
    }

}