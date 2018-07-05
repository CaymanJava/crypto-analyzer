package pro.crypto.indicator.rsi;

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
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;

public class ConnorsRelativeStrengthIndexTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testConnorsRelativeStrengthIndexWithExponentialMovingAverageAndDefaultPeriods() {
        RSIResult[] result = new ConnorsRelativeStrengthIndex(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[5].getIndicatorValue());
        assertNull(result[18].getIndicatorValue());
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(12.7399955918));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(65.7254405611));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(77.3575478734));
        assertEquals(result[63].getTime(), of(2018, 4, 29, 0, 0));
        assertEquals(result[63].getIndicatorValue(), toBigDecimal(63.0840070673));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(10.0078264472));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, size: {0}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[0])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(null)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void simpleRSIPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, period: {20}, size: {19}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[19])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .simpleRsiPeriod(20)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void streakRSIPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, period: {20}, size: {19}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[19])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(20)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void percentRankPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, period: {20}, size: {19}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[19])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void simpleRSIPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, period: {-3}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .simpleRsiPeriod(-3)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void streakRSIPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, period: {-2}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(-2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void percentRankPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, period: {-20}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(2)
                .percentRankPeriod(-20)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    private CRSIRequest buildRequest() {
        return CRSIRequest.builder()
                .originalData(originalData)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build();
    }

}