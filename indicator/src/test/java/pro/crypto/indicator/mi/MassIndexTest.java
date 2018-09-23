package pro.crypto.indicator.mi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class MassIndexTest extends IndicatorAbstractTest {

    @Test
    public void testMassIndexWithPeriodTwentyFive() {
        MIResult[] result = new MassIndex(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[20].getIndicatorValue());
        assertNull(result[39].getIndicatorValue());
        assertEquals(result[40].getTime(), of(2018, 4, 6, 0, 0));
        assertEquals(result[40].getIndicatorValue(), toBigDecimal(25.6318207569));
        assertEquals(result[48].getTime(), of(2018, 4, 14, 0, 0));
        assertEquals(result[48].getIndicatorValue(), toBigDecimal(24.8350865791));
        assertEquals(result[52].getTime(), of(2018, 4, 18, 0, 0));
        assertEquals(result[52].getIndicatorValue(), toBigDecimal(24.8189733444));
        assertEquals(result[65].getTime(), of(2018, 5, 1, 0, 0));
        assertEquals(result[65].getIndicatorValue(), toBigDecimal(24.7936907834));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(24.8625295148));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {MASS_INDEX}, size: {0}}");
        new MassIndex(MIRequest.builder()
                .originalData(new Tick[0])
                .period(25)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {MASS_INDEX}}");
        new MassIndex(MIRequest.builder()
                .originalData(null)
                .period(25)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {MASS_INDEX}, period: {43}, size: {42}}");
        new MassIndex(MIRequest.builder()
                .originalData(new Tick[42])
                .period(25)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {MASS_INDEX}, period: {-25}");
        new MassIndex(MIRequest.builder()
                .originalData(new Tick[100])
                .period(-25)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return MIRequest.builder()
                .originalData(originalData)
                .period(25)
                .build();
    }

}
