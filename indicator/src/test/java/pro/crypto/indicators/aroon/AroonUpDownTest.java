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

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertTrue(isNull(result[0].getAroonUp()));
        assertTrue(isNull(result[0].getAroonDown()));
        assertTrue(isNull(result[0].getAroonOscillator()));
        assertTrue(isNull(result[5].getAroonUp()));
        assertTrue(isNull(result[5].getAroonDown()));
        assertTrue(isNull(result[5].getAroonOscillator()));
        assertTrue(isNull(result[13].getAroonUp()));
        assertTrue(isNull(result[13].getAroonDown()));
        assertTrue(isNull(result[13].getAroonOscillator()));
        assertEquals(result[14].getTime(), of(2018, 3, 11, 0, 0));
        assertEquals(result[14].getAroonUp(), new BigDecimal(42.8571428600).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[14].getAroonDown(), new BigDecimal(100.0000000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[14].getAroonOscillator(), new BigDecimal(-57.1428571400).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[26].getTime(), of(2018, 3, 23, 0, 0));
        assertEquals(result[26].getAroonUp(), new BigDecimal(0E-10).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[26].getAroonDown(), new BigDecimal(92.8571428600).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[26].getAroonOscillator(), new BigDecimal(-92.8571428600).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getAroonUp(), new BigDecimal(100.0000000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getAroonDown(), new BigDecimal(64.2857142900).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getAroonOscillator(), new BigDecimal(35.7142857100).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[47].getTime(), of(2018, 4, 13, 0, 0));
        assertEquals(result[47].getAroonUp(), new BigDecimal(78.5714285700).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[47].getAroonDown(), new BigDecimal(0E-10).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[47].getAroonOscillator(), new BigDecimal(78.5714285700).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getAroonUp(), new BigDecimal(92.8571428600).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[64].getAroonDown(), new BigDecimal(0E-10).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[64].getAroonOscillator(), new BigDecimal(92.8571428600).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getAroonUp(), new BigDecimal(35.7142857100).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getAroonDown(), new BigDecimal(100.0000000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getAroonOscillator(), new BigDecimal(-64.2857142900).setScale(10, BigDecimal.ROUND_HALF_UP));
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