package pro.crypto.indicators.adx;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.ADXRequest;
import pro.crypto.model.result.ADXResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class AverageDirectionalMovementIndexTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testADXWithPeriodFourteen() {
        ADXResult[] result = new AverageDirectionalMovementIndex(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getAverageDirectionalIndex());
        assertNull(result[0].getPositiveDirectionalIndicator());
        assertNull(result[0].getNegativeDirectionalIndicator());
        assertNull(result[5].getAverageDirectionalIndex());
        assertNull(result[5].getPositiveDirectionalIndicator());
        assertNull(result[5].getNegativeDirectionalIndicator());
        assertNull(result[12].getAverageDirectionalIndex());
        assertNull(result[12].getPositiveDirectionalIndicator());
        assertNull(result[12].getNegativeDirectionalIndicator());
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getPositiveDirectionalIndicator(), toBigDecimal(13.20751923));
        assertEquals(result[13].getNegativeDirectionalIndicator(), toBigDecimal(30.51786889));
        assertNull(result[13].getAverageDirectionalIndex());
        assertEquals(result[26].getTime(), of(2018, 3, 23, 0, 0));
        assertEquals(result[26].getPositiveDirectionalIndicator(), toBigDecimal(9.26718303));
        assertEquals(result[26].getNegativeDirectionalIndicator(), toBigDecimal(34.05796257));
        assertEquals(result[26].getAverageDirectionalIndex(), toBigDecimal(43.1045848871));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getPositiveDirectionalIndicator(), toBigDecimal(32.59615249));
        assertEquals(result[32].getNegativeDirectionalIndicator(), toBigDecimal(28.93427285));
        assertEquals(result[32].getAverageDirectionalIndex(), toBigDecimal(24.9081703821));
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getPositiveDirectionalIndicator(), toBigDecimal(29.78767483));
        assertEquals(result[64].getNegativeDirectionalIndicator(), toBigDecimal(24.64517792));
        assertEquals(result[64].getAverageDirectionalIndex(), toBigDecimal(26.223985116));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getPositiveDirectionalIndicator(), toBigDecimal(14.92485249));
        assertEquals(result[72].getNegativeDirectionalIndicator(), toBigDecimal(34.34646336));
        assertEquals(result[72].getAverageDirectionalIndex(), toBigDecimal(27.2733245358));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {AVERAGE_DIRECTIONAL_MOVEMENT_INDEX}, size: {0}}");
        new AverageDirectionalMovementIndex(ADXRequest.builder()
                .originalData(new Tick[0])
                .period(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {AVERAGE_DIRECTIONAL_MOVEMENT_INDEX}}");
        new AverageDirectionalMovementIndex(ADXRequest.builder()
                .originalData(null)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {AVERAGE_DIRECTIONAL_MOVEMENT_INDEX}, period: {20}, size: {19}}");
        new AverageDirectionalMovementIndex(ADXRequest.builder()
                .originalData(new Tick[19])
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {AVERAGE_DIRECTIONAL_MOVEMENT_INDEX}, period: {-10}}");
        new AverageDirectionalMovementIndex(ADXRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .build()).getResult();
    }

    private ADXRequest buildRequest() {
        return ADXRequest.builder()
                .originalData(originalData)
                .period(14)
                .build();
    }

}