package pro.crypto.indicator.macd;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class MovingAverageConvergenceDivergenceTest extends IndicatorAbstractTest {

    @Test
    public void testMovingAverageConvergenceDivergence() {
        MACDResult[] result = new MovingAverageConvergenceDivergence(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineResult());
        assertNull(result[0].getBarChartValue());
        assertNull(result[24].getIndicatorValue());
        assertNull(result[24].getSignalLineResult());
        assertNull(result[24].getBarChartValue());
        assertEquals(result[25].getTime(), of(2018, 3, 22, 0, 0));
        assertEquals(result[25].getIndicatorValue(), toBigDecimal(-53.2538699758));
        assertNull(result[25].getSignalLineResult());
        assertNull(result[25].getBarChartValue());
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(-13.557054726));
        assertNull(result[32].getSignalLineResult());
        assertNull(result[32].getBarChartValue());
        assertEquals(result[33].getTime(), of(2018, 3, 30, 0, 0));
        assertEquals(result[33].getIndicatorValue(), toBigDecimal(-6.5494925671));
        assertEquals(result[33].getSignalLineResult(), toBigDecimal(-33.7846225485));
        assertEquals(result[33].getBarChartValue(), toBigDecimal(27.2351299814));
        assertEquals(result[38].getTime(), of(2018, 4, 4, 0, 0));
        assertEquals(result[38].getIndicatorValue(), toBigDecimal(17.5923276967));
        assertEquals(result[38].getSignalLineResult(), toBigDecimal(-3.4887882607));
        assertEquals(result[38].getBarChartValue(), toBigDecimal(21.0811159574));
        assertEquals(result[44].getTime(), of(2018, 4, 10, 0, 0));
        assertEquals(result[44].getIndicatorValue(), toBigDecimal(31.9319044814));
        assertEquals(result[44].getSignalLineResult(), toBigDecimal(17.8573326025));
        assertEquals(result[44].getBarChartValue(), toBigDecimal(14.0745718789));
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getIndicatorValue(), toBigDecimal(32.094978201));
        assertEquals(result[49].getSignalLineResult(), toBigDecimal(30.4281777488));
        assertEquals(result[49].getBarChartValue(), toBigDecimal(1.6668004522));
        assertEquals(result[61].getTime(), of(2018, 4, 27, 0, 0));
        assertEquals(result[61].getIndicatorValue(), toBigDecimal(44.8499553601));
        assertEquals(result[61].getSignalLineResult(), toBigDecimal(38.8860027323));
        assertEquals(result[61].getBarChartValue(), toBigDecimal(5.9639526278));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(6.5633752317));
        assertEquals(result[72].getSignalLineResult(), toBigDecimal(20.6154250793));
        assertEquals(result[72].getBarChartValue(), toBigDecimal(-14.0520498476));
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

    @Override
    protected IndicatorRequest buildRequest() {
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
