package pro.crypto.indicator.eom;

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
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;

public class EaseOfMovementTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testEaseOfMovementWithPeriodFourteen() {
        EOMResult[] result = new EaseOfMovement(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[8].getIndicatorValue());
        assertNull(result[13].getIndicatorValue());
        assertEquals(result[14].getTime(), of(2018, 3, 11, 0, 0));
        assertEquals(result[14].getIndicatorValue(), toBigDecimal(-3.6126264897));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-4.2036821021));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(51.2471680053));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(3.5968325448));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(2.8073664577));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-7.9507970083));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {EASE_OF_MOVEMENT}, size: {0}}");
        new EaseOfMovement(EOMRequest.builder()
                .originalData(new Tick[0])
                .movingAveragePeriod(14)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {EASE_OF_MOVEMENT}}");
        new EaseOfMovement(EOMRequest.builder()
                .originalData(null)
                .movingAveragePeriod(14)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void movingAveragePeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {EASE_OF_MOVEMENT}, period: {14}, size: {13}}");
        new EaseOfMovement(EOMRequest.builder()
                .originalData(new Tick[13])
                .movingAveragePeriod(14)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void movingAveragePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {EASE_OF_MOVEMENT}, period: {-14}}");
        new EaseOfMovement(EOMRequest.builder()
                .originalData(new Tick[100])
                .movingAveragePeriod(-14)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {EASE_OF_MOVEMENT}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new EaseOfMovement(EOMRequest.builder()
                .originalData(new Tick[100])
                .movingAveragePeriod(14)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .build()).getResult();
    }

    private EOMRequest buildRequest() {
        return EOMRequest.builder()
                .originalData(originalData)
                .movingAveragePeriod(14)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build();
    }

}