package pro.crypto.indicators.aroon;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.AroonRequest;
import pro.crypto.model.result.AroonResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class AroonUpDownTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAroonWithPeriodFourteen() {
        AroonResult[] result = new AroonUpDown(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getAroonUp());
        assertNull(result[0].getAroonDown());
        assertNull(result[0].getAroonOscillator());
        assertNull(result[5].getAroonUp());
        assertNull(result[5].getAroonDown());
        assertNull(result[5].getAroonOscillator());
        assertNull(result[13].getAroonUp());
        assertNull(result[13].getAroonDown());
        assertNull(result[13].getAroonOscillator());
        assertEquals(result[14].getTime(), of(2018, 3, 11, 0, 0));
        assertEquals(result[14].getAroonUp(), toBigDecimal(42.8571428571));
        assertEquals(result[14].getAroonDown(), toBigDecimal(100.0));
        assertEquals(result[14].getAroonOscillator(), toBigDecimal(-57.1428571429));
        assertEquals(result[26].getTime(), of(2018, 3, 23, 0, 0));
        assertEquals(result[26].getAroonUp(), toBigDecimal(0.0));
        assertEquals(result[26].getAroonDown(), toBigDecimal(92.8571428571));
        assertEquals(result[26].getAroonOscillator(), toBigDecimal(-92.8571428571));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getAroonUp(), toBigDecimal(100.0));
        assertEquals(result[32].getAroonDown(), toBigDecimal(64.2857142857));
        assertEquals(result[32].getAroonOscillator(), toBigDecimal(35.7142857143));
        assertEquals(result[47].getTime(), of(2018, 4, 13, 0, 0));
        assertEquals(result[47].getAroonUp(), toBigDecimal(78.5714285714));
        assertEquals(result[47].getAroonDown(), toBigDecimal(0.0));
        assertEquals(result[47].getAroonOscillator(), toBigDecimal(78.5714285714));
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getAroonUp(), toBigDecimal(92.8571428571));
        assertEquals(result[64].getAroonDown(), toBigDecimal(0.0));
        assertEquals(result[64].getAroonOscillator(), toBigDecimal(92.8571428571));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getAroonUp(), toBigDecimal(35.7142857143));
        assertEquals(result[72].getAroonDown(), toBigDecimal(100.0));
        assertEquals(result[72].getAroonOscillator(), toBigDecimal(-64.2857142857));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {AROON_UP_DOWN}, size: {0}}");
        new AroonUpDown(AroonRequest.builder()
                .originalData(new Tick[0])
                .period(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {AROON_UP_DOWN}}");
        new AroonUpDown(AroonRequest.builder()
                .originalData(null)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {AROON_UP_DOWN}, period: {20}, size: {19}}");
        new AroonUpDown(AroonRequest.builder()
                .originalData(new Tick[19])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {AROON_UP_DOWN}, period: {-14}");
        new AroonUpDown(AroonRequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .build()).getResult();
    }

    private AroonRequest buildRequest() {
        return AroonRequest.builder()
                .originalData(originalData)
                .period(14)
                .build();
    }

}