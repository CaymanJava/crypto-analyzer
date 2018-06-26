package pro.crypto.indicators.macd;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithClosePriceGenerator;
import pro.crypto.model.request.MACDRequest;
import pro.crypto.model.result.MACDResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class MovingAverageConvergenceDivergenceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithClosePriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testMACDWithRecommendedPeriods() {
        MACDResult[] result = new MovingAverageConvergenceDivergence(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineResult());
        assertNull(result[0].getBarChartValue());
        assertNull(result[24].getIndicatorValue());
        assertNull(result[24].getSignalLineResult());
        assertNull(result[24].getBarChartValue());
        assertEquals(result[25].getTime(), of(2018, 3, 22, 0, 0));
        assertEquals(result[25].getIndicatorValue(), toBigDecimal(0.4056563357));
        assertNull(result[25].getSignalLineResult());
        assertNull(result[25].getBarChartValue());
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(0.2846054137));
        assertNull(result[32].getSignalLineResult());
        assertNull(result[32].getBarChartValue());
        assertEquals(result[33].getTime(), of(2018, 3, 30, 0, 0));
        assertEquals(result[33].getIndicatorValue(), toBigDecimal(0.1889216936));
        assertEquals(result[33].getSignalLineResult(), toBigDecimal(0.3917744361));
        assertEquals(result[33].getBarChartValue(), toBigDecimal(-0.2028527425));
        assertEquals(result[38].getTime(), of(2018, 4, 4, 0, 0));
        assertEquals(result[38].getIndicatorValue(), toBigDecimal(0.1968265092));
        assertEquals(result[38].getSignalLineResult(), toBigDecimal(0.2394121086));
        assertEquals(result[38].getBarChartValue(), toBigDecimal(-0.0425855994));
        assertEquals(result[44].getTime(), of(2018, 4, 10, 0, 0));
        assertEquals(result[44].getIndicatorValue(), toBigDecimal(0.3922420233));
        assertEquals(result[44].getSignalLineResult(), toBigDecimal(0.2972576828));
        assertEquals(result[44].getBarChartValue(), toBigDecimal(0.0949843405));
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getIndicatorValue(), toBigDecimal(0.1824057756));
        assertEquals(result[49].getSignalLineResult(), toBigDecimal(0.3203577637));
        assertEquals(result[49].getBarChartValue(), toBigDecimal(-0.1379519881));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, size: {0}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[0])
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(null)
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptySlowPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, period: {0}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptyFastPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, period: {0}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(12)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptySignalPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, period: {0}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(12)
                .fastPeriod(26)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}}, movingAverageType: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(MOVING_AVERAGE_CONVERGENCE_DIVERGENCE)
                .build()).getResult();
    }

    @Test
    public void notEnoughDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, period: {35}, size: {10}}");
        new MovingAverageConvergenceDivergence(MACDRequest.builder()
                .originalData(new Tick[10])
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    private MACDRequest buildRequest() {
        return MACDRequest.builder()
                .originalData(originalData)
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

}