package pro.crypto.indicator.eft;

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

public class EhlersFisherTransformTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testEhlersFisherTransformWithDefaultPeriod() {
        EFTResult[] result = new EhlersFisherTransform(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getTrigger());
        assertNull(result[9].getIndicatorValue());
        assertNull(result[9].getTrigger());
        assertEquals(result[10].getTime(), of(2018, 3, 7, 0, 0));
        assertEquals(result[10].getIndicatorValue(), toBigDecimal(-0.1411989406));
        assertNull(result[10].getTrigger());
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getIndicatorValue(), toBigDecimal(-1.0862365190));
        assertEquals(result[13].getTrigger(), toBigDecimal(-0.7417553536));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-1.1377743519));
        assertEquals(result[19].getTrigger(), toBigDecimal(-1.0896211008));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(0.9010729753));
        assertEquals(result[32].getTrigger(), toBigDecimal(0.5198781337));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(1.3988614077));
        assertEquals(result[45].getTrigger(), toBigDecimal(1.3788430911));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(1.1934769494));
        assertEquals(result[58].getTrigger(), toBigDecimal(0.9500840595));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-1.3796816085));
        assertEquals(result[72].getTrigger(), toBigDecimal(-1.1722108547));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {EHLERS_FISHER_TRANSFORM}, size: {0}}");
        new EhlersFisherTransform(EFTRequest.builder()
                .originalData(new Tick[0])
                .period(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {EHLERS_FISHER_TRANSFORM}}");
        new EhlersFisherTransform(EFTRequest.builder()
                .originalData(null)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {EHLERS_FISHER_TRANSFORM}, period: {10}, size: {9}}");
        new EhlersFisherTransform(EFTRequest.builder()
                .originalData(new Tick[9])
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {EHLERS_FISHER_TRANSFORM}, period: {-10}");
        new EhlersFisherTransform(EFTRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .build()).getResult();
    }

    private EFTRequest buildRequest() {
        return EFTRequest.builder()
                .originalData(originalData)
                .period(10)
                .build();
    }

}