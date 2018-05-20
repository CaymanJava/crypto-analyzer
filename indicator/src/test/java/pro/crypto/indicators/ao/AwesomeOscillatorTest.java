package pro.crypto.indicators.ao;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.AORequest;
import pro.crypto.model.result.AOResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class AwesomeOscillatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAwesomeOscillator() {
        AOResult[] result = new AwesomeOscillator(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getIncreased());
        assertNull(result[32].getIndicatorValue());
        assertNull(result[32].getIncreased());
        assertEquals(result[33].getTime(), of(2018, 3, 30, 0, 0));
        assertEquals(result[33].getIndicatorValue(), toBigDecimal(23.6640029412));
        assertFalse(result[33].getIncreased());
        assertEquals(result[48].getTime(), of(2018, 4, 14, 0, 0));
        assertEquals(result[48].getIndicatorValue(), toBigDecimal(132.2333335294));
        assertTrue(result[48].getIncreased());
        assertEquals(result[53].getTime(), of(2018, 4, 19, 0, 0));
        assertEquals(result[53].getIndicatorValue(), toBigDecimal(83.4718050000));
        assertFalse(result[53].getIncreased());
        assertEquals(result[61].getTime(), of(2018, 4, 27, 0, 0));
        assertEquals(result[61].getIndicatorValue(), toBigDecimal(114.7475208824));
        assertTrue(result[61].getIncreased());
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-10.6772100000));
        assertFalse(result[72].getIncreased());
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {AWESOME_OSCILLATOR}, size: {0}}");
        new AwesomeOscillator(AORequest.builder()
                .originalData(new Tick[0])
                .slowPeriod(5)
                .fastPeriod(34)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {AWESOME_OSCILLATOR}}");
        new AwesomeOscillator(AORequest.builder()
                .originalData(null)
                .slowPeriod(5)
                .fastPeriod(34)
                .build()).getResult();
    }

    @Test
    public void originalDataSizeLessThanSlowPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {AWESOME_OSCILLATOR}, period: {5}, size: {4}}");
        new AwesomeOscillator(AORequest.builder()
                .originalData(new Tick[4])
                .slowPeriod(5)
                .fastPeriod(34)
                .build()).getResult();
    }

    @Test
    public void originalDataSizeLessThanFastPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {AWESOME_OSCILLATOR}, period: {34}, size: {33}}");
        new AwesomeOscillator(AORequest.builder()
                .originalData(new Tick[33])
                .slowPeriod(5)
                .fastPeriod(34)
                .build()).getResult();
    }

    private AORequest buildRequest() {
        return AORequest.builder()
                .originalData(originalData)
                .slowPeriod(5)
                .fastPeriod(34)
                .build();
    }

}