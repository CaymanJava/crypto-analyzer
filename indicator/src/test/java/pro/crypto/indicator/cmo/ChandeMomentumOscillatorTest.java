package pro.crypto.indicator.cmo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class ChandeMomentumOscillatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testChandeMomentumOscillatorWithPeriodNine() {
        CMOResult[] result = new ChandeMomentumOscillator(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[7].getIndicatorValue());
        assertEquals(result[8].getTime(), of(2018, 3, 5, 0, 0));
        assertEquals(result[8].getIndicatorValue(), toBigDecimal(14.4293042625));
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertEquals(result[20].getIndicatorValue(), toBigDecimal(-18.1822657347));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(43.6024395497));
        assertEquals(result[34].getTime(), of(2018, 3, 31, 0, 0));
        assertEquals(result[34].getIndicatorValue(), toBigDecimal(58.0817882728));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(43.7057838743));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-58.0739250596));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CHANDE_MOMENTUM_OSCILLATOR}, size: {0}}");
        new ChandeMomentumOscillator(CMORequest.builder()
                .originalData(new Tick[0])
                .period(9)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CHANDE_MOMENTUM_OSCILLATOR}}");
        new ChandeMomentumOscillator(CMORequest.builder()
                .originalData(null)
                .period(9)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CHANDE_MOMENTUM_OSCILLATOR}, period: {9}, size: {8}}");
        new ChandeMomentumOscillator(CMORequest.builder()
                .originalData(new Tick[8])
                .period(9)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHANDE_MOMENTUM_OSCILLATOR}, period: {-9}}");
        new ChandeMomentumOscillator(CMORequest.builder()
                .originalData(new Tick[100])
                .period(-9)
                .build()).getResult();
    }

    private CMORequest buildRequest() {
        return CMORequest.builder()
                .originalData(originalData)
                .period(9)
                .build();
    }

}