package pro.crypto.indicator.smi;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;

public class StochasticMomentumIndexTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testStochasticMomentumIndexWithTenAndThreePeriods() {
        SMIResult[] result = new StochasticMomentumIndex(buildRequest(10, 3)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineValue());
        assertNull(result[14].getIndicatorValue());
        assertNull(result[14].getSignalLineValue());
        assertEquals(result[15].getTime(), of(2018, 3, 12, 0, 0));
        assertEquals(result[15].getIndicatorValue(), toBigDecimal(-84.3893180320));
        assertNull(result[15].getSignalLineValue());
        assertEquals(result[17].getTime(), of(2018, 3, 14, 0, 0));
        assertEquals(result[17].getIndicatorValue(), toBigDecimal(-67.2608583337));
        assertEquals(result[17].getSignalLineValue(), toBigDecimal(-76.6225712826));
        assertEquals(result[33].getTime(), of(2018, 3, 30, 0, 0));
        assertEquals(result[33].getIndicatorValue(), toBigDecimal(63.8003185515));
        assertEquals(result[33].getSignalLineValue(), toBigDecimal(47.6172524002));
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getIndicatorValue(), toBigDecimal(43.6790044904));
        assertEquals(result[49].getSignalLineValue(), toBigDecimal(55.3226786299));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(64.8333526054));
        assertEquals(result[58].getSignalLineValue(), toBigDecimal(54.3933277281));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-65.7067729388));
        assertEquals(result[72].getSignalLineValue(), toBigDecimal(-57.3561191341));
    }

    @Test
    public void testStochasticMomentumIndexWithFourteenAndFourPeriods() {
        SMIResult[] result = new StochasticMomentumIndex(buildRequest(14, 4)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineValue());
        assertNull(result[21].getIndicatorValue());
        assertNull(result[21].getSignalLineValue());
        assertEquals(result[22].getTime(), of(2018, 3, 19, 0, 0));
        assertEquals(result[22].getIndicatorValue(), toBigDecimal(-64.8195965794));
        assertNull(result[22].getSignalLineValue());
        assertEquals(result[25].getTime(), of(2018, 3, 22, 0, 0));
        assertEquals(result[25].getIndicatorValue(), toBigDecimal(-73.8959879309));
        assertEquals(result[25].getSignalLineValue(), toBigDecimal(-69.3298997122));
        assertEquals(result[33].getTime(), of(2018, 3, 30, 0, 0));
        assertEquals(result[33].getIndicatorValue(), toBigDecimal(33.1749480855));
        assertEquals(result[33].getSignalLineValue(), toBigDecimal(6.5240193164));
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getIndicatorValue(), toBigDecimal(63.5608047162));
        assertEquals(result[49].getSignalLineValue(), toBigDecimal(71.9237208806));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(58.9210497142));
        assertEquals(result[58].getSignalLineValue(), toBigDecimal(51.9375723901));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-39.8419668352));
        assertEquals(result[72].getSignalLineValue(), toBigDecimal(-19.9088000862));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {STOCHASTIC_MOMENTUM_INDEX}, size: {0}}");
        new StochasticMomentumIndex(SMIRequest.builder()
                .originalData(new Tick[0])
                .period(10)
                .smoothingPeriod(3)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {STOCHASTIC_MOMENTUM_INDEX}}");
        new StochasticMomentumIndex(SMIRequest.builder()
                .originalData(null)
                .period(10)
                .smoothingPeriod(3)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {STOCHASTIC_MOMENTUM_INDEX}, period: {17}, size: {16}}");
        new StochasticMomentumIndex(SMIRequest.builder()
                .originalData(new Tick[16])
                .period(10)
                .smoothingPeriod(3)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {STOCHASTIC_MOMENTUM_INDEX}, period: {-10}");
        new StochasticMomentumIndex(SMIRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .smoothingPeriod(3)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void smoothingPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {STOCHASTIC_MOMENTUM_INDEX}, period: {-3}");
        new StochasticMomentumIndex(SMIRequest.builder()
                .originalData(new Tick[100])
                .period(10)
                .smoothingPeriod(-3)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {STOCHASTIC_MOMENTUM_INDEX}}, movingAverageType: {AVERAGE_TRUE_RANGE}");
        new StochasticMomentumIndex(SMIRequest.builder()
                .originalData(new Tick[100])
                .period(10)
                .smoothingPeriod(3)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .build()).getResult();
    }

    private IndicatorRequest buildRequest(int period, int smoothingPeriod) {
        return SMIRequest.builder()
                .originalData(originalData)
                .period(period)
                .smoothingPeriod(smoothingPeriod)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

}