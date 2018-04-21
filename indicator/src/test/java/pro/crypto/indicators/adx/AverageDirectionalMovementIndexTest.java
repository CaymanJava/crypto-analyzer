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

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertTrue(isNull(result[0].getAverageDirectionalIndex()));
        assertTrue(isNull(result[0].getPositiveDirectionalIndicator()));
        assertTrue(isNull(result[0].getNegativeDirectionalIndicator()));
        assertTrue(isNull(result[5].getAverageDirectionalIndex()));
        assertTrue(isNull(result[5].getPositiveDirectionalIndicator()));
        assertTrue(isNull(result[5].getNegativeDirectionalIndicator()));
        assertTrue(isNull(result[12].getAverageDirectionalIndex()));
        assertTrue(isNull(result[12].getPositiveDirectionalIndicator()));
        assertTrue(isNull(result[12].getNegativeDirectionalIndicator()));
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getPositiveDirectionalIndicator(), new BigDecimal(13.2075192300).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[13].getNegativeDirectionalIndicator(), new BigDecimal(30.5178688900).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[13].getAverageDirectionalIndex()));
        assertEquals(result[26].getTime(), of(2018, 3, 23, 0, 0));
        assertEquals(result[26].getPositiveDirectionalIndicator(), new BigDecimal(9.2671830300).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[26].getNegativeDirectionalIndicator(), new BigDecimal(34.0579625700).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[26].getAverageDirectionalIndex(), new BigDecimal(43.1045848871).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getPositiveDirectionalIndicator(), new BigDecimal(32.5961524900).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getNegativeDirectionalIndicator(), new BigDecimal(28.9342728500).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getAverageDirectionalIndex(), new BigDecimal(24.9081703821).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getPositiveDirectionalIndicator(), new BigDecimal(29.7876748300).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[64].getNegativeDirectionalIndicator(), new BigDecimal(24.6451779200).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[64].getAverageDirectionalIndex(), new BigDecimal(26.2239851160).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getPositiveDirectionalIndicator(), new BigDecimal(14.9248524900).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getNegativeDirectionalIndicator(), new BigDecimal(34.3464633600).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getAverageDirectionalIndex(), new BigDecimal(27.2733245358).setScale(10, BigDecimal.ROUND_HALF_UP));
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