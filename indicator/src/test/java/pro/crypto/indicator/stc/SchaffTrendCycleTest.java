package pro.crypto.indicator.stc;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.IncreasedQuantityTickGenerator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.MOVING_AVERAGE_CONVERGENCE_DIVERGENCE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class SchaffTrendCycleTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new IncreasedQuantityTickGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testSchaffTrendCycleWithDefaultParams() {
        STCResult[] result = new SchaffTrendCycle(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[45].getIndicatorValue());
        assertNull(result[66].getIndicatorValue());
        assertEquals(result[67].getTime(), of(2018, 5, 3, 0, 0));
        assertEquals(result[67].getIndicatorValue(), toBigDecimal(88.5115762881));
        assertEquals(result[75].getTime(), of(2018, 5, 11, 0, 0));
        assertEquals(result[75].getIndicatorValue(), toBigDecimal(0.3457483449));
        assertEquals(result[89].getTime(), of(2018, 5, 25, 0, 0));
        assertEquals(result[89].getIndicatorValue(), toBigDecimal(87.5000211052));
        assertEquals(result[100].getTime(), of(2018, 6, 5, 0, 0));
        assertEquals(result[100].getIndicatorValue(), toBigDecimal(99.3940337382));
        assertEquals(result[116].getTime(), of(2018, 6, 21, 0, 0));
        assertEquals(result[116].getIndicatorValue(), toBigDecimal(0.3906157537));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {SCHAFF_TREND_CYCLE}, size: {0}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(23)
                .longCycle(50)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {SCHAFF_TREND_CYCLE}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(23)
                .longCycle(50)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {SCHAFF_TREND_CYCLE}, period: {70}, size: {69}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[69])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(23)
                .longCycle(50)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {SCHAFF_TREND_CYCLE}, period: {-10}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(-10)
                .shortCycle(23)
                .longCycle(50)
                .build()).getResult();
    }

    @Test
    public void shortCycleLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {SCHAFF_TREND_CYCLE}, period: {-23}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(-23)
                .longCycle(50)
                .build()).getResult();
    }

    @Test
    public void longCycleLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {SCHAFF_TREND_CYCLE}, period: {-50}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(23)
                .longCycle(-50)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {SCHAFF_TREND_CYCLE}}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(23)
                .longCycle(50)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {SCHAFF_TREND_CYCLE}}," +
                " movingAverageType: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}");
        new SchaffTrendCycle(STCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(MOVING_AVERAGE_CONVERGENCE_DIVERGENCE)
                .period(10)
                .shortCycle(23)
                .longCycle(50)
                .build()).getResult();
    }

    private IndicatorRequest buildRequest() {
        return STCRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .period(10)
                .shortCycle(23)
                .longCycle(50)
                .build();
    }

}