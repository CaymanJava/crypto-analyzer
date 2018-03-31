package pro.crypto.indicators.uo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.UORequest;
import pro.crypto.model.result.UOResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UltimateOscillatorTest {


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testUOWithDefaultPeriods() {
        UOResult[] result = new UltimateOscillator(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[10].getIndicatorValue()));
        assertTrue(isNull(result[27].getIndicatorValue()));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getIndicatorValue(), new BigDecimal(48.8058991100).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), new BigDecimal(68.4482305900).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), new BigDecimal(64.4682879900).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), new BigDecimal(40.1867732200).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ULTIMATE_OSCILLATOR}, size: {0}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[0])
                .shortPeriod(7)
                .middlePeriod(14)
                .longPeriod(28)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ULTIMATE_OSCILLATOR}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(null)
                .shortPeriod(7)
                .middlePeriod(14)
                .longPeriod(28)
                .build()).getResult();
    }

    @Test
    public void shortPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ULTIMATE_OSCILLATOR}, period: {-7}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(-7)
                .middlePeriod(14)
                .longPeriod(28)
                .build()).getResult();
    }

    @Test
    public void middlePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ULTIMATE_OSCILLATOR}, period: {-14}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(7)
                .middlePeriod(-14)
                .longPeriod(28)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ULTIMATE_OSCILLATOR}, period: {-28}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[100])
                .shortPeriod(7)
                .middlePeriod(14)
                .longPeriod(-28)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is not enough {indicator: {ULTIMATE_OSCILLATOR}, tickLength: {19}, fastPeriod: {28}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[19])
                .shortPeriod(7)
                .middlePeriod(14)
                .longPeriod(28)
                .build()).getResult();
    }

    @Test
    public void incorrectShortPeriodLengthTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incorrect period values {indicator: {ULTIMATE_OSCILLATOR}, shortPeriod: {16}, middlePeriod: {14}}, longPeriod: {28}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[19])
                .shortPeriod(16)
                .middlePeriod(14)
                .longPeriod(28)
                .build()).getResult();
    }

    @Test
    public void incorrectMiddlePeriodLengthTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incorrect period values {indicator: {ULTIMATE_OSCILLATOR}, shortPeriod: {7}, middlePeriod: {29}}, longPeriod: {28}}");
        new UltimateOscillator(UORequest.builder()
                .originalData(new Tick[19])
                .shortPeriod(7)
                .middlePeriod(29)
                .longPeriod(28)
                .build()).getResult();
    }

    private UORequest buildRequest() {
        return UORequest.builder()
                .originalData(originalData)
                .shortPeriod(7)
                .middlePeriod(14)
                .longPeriod(28)
                .build();
    }

}