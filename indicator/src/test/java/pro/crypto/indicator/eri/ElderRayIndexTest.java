package pro.crypto.indicator.eri;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class ElderRayIndexTest extends IndicatorAbstractTest {

    @Test
    public void testElderRayIndexWithPeriodThirteen() {
        ERIResult[] result = new ElderRayIndex(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getBullPower());
        assertNull(result[0].getBearPower());
        assertNull(result[11].getBullPower());
        assertNull(result[11].getBearPower());
        assertEquals(result[12].getTime(), of(2018, 3, 9, 0, 0));
        assertEquals(result[12].getBullPower(), toBigDecimal(-35.4030769231));
        assertEquals(result[12].getBearPower(), toBigDecimal(-62.1231769231));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getBullPower(), toBigDecimal(-34.8828980410));
        assertEquals(result[19].getBearPower(), toBigDecimal(-65.5529980410));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getBullPower(), toBigDecimal(71.4531664051));
        assertEquals(result[32].getBearPower(), toBigDecimal(51.7031664051));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getBullPower(), toBigDecimal(73.0123200028));
        assertEquals(result[45].getBearPower(), toBigDecimal(50.9723200028));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getBullPower(), toBigDecimal(74.5665332363));
        assertEquals(result[58].getBearPower(), toBigDecimal(48.7165332363));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getBullPower(), toBigDecimal(-19.1831772583));
        assertEquals(result[72].getBearPower(), toBigDecimal(-44.3330772583));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ELDER_RAY_INDEX}, size: {0}}");
        new ElderRayIndex(ERIRequest.builder()
                .originalData(new Tick[0])
                .period(13)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ELDER_RAY_INDEX}}");
        new ElderRayIndex(ERIRequest.builder()
                .originalData(null)
                .period(13)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ELDER_RAY_INDEX}, period: {13}, size: {12}}");
        new ElderRayIndex(ERIRequest.builder()
                .originalData(new Tick[12])
                .period(13)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ELDER_RAY_INDEX}, period: {-13}");
        new ElderRayIndex(ERIRequest.builder()
                .originalData(new Tick[100])
                .period(-13)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return ERIRequest.builder()
                .originalData(originalData)
                .period(13)
                .build();
    }

}
