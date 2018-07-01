package pro.crypto.indicators.ppo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.PPORequest;
import pro.crypto.model.result.PPOResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class PercentagePriceOscillatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testPercentagePriceOscillatorWithRecommendedPeriods() {
        PPOResult[] result = new PercentagePriceOscillator(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineValue());
        assertNull(result[0].getBarChartValue());
        assertNull(result[24].getIndicatorValue());
        assertNull(result[24].getSignalLineValue());
        assertNull(result[24].getBarChartValue());
        assertEquals(result[25].getTime(), of(2018, 3, 22, 0, 0));
        assertEquals(result[25].getIndicatorValue(), toBigDecimal(-4.3300594751));
        assertNull(result[25].getSignalLineValue());
        assertNull(result[25].getBarChartValue());
        assertEquals(result[33].getTime(), of(2018, 3, 30, 0, 0));
        assertEquals(result[33].getIndicatorValue(), toBigDecimal(-0.5321626928));
        assertEquals(result[33].getSignalLineValue(), toBigDecimal(-2.7661095387));
        assertEquals(result[33].getBarChartValue(), toBigDecimal(2.2339468459));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(2.0870125198));
        assertEquals(result[43].getSignalLineValue(), toBigDecimal(1.1186642718));
        assertEquals(result[43].getBarChartValue(), toBigDecimal(0.9683482480));
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getIndicatorValue(), toBigDecimal(2.4410375294));
        assertEquals(result[49].getSignalLineValue(), toBigDecimal(2.3315850277));
        assertEquals(result[49].getBarChartValue(), toBigDecimal(0.1094525017));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(2.9638715578));
        assertEquals(result[58].getSignalLineValue(), toBigDecimal(2.5480082842));
        assertEquals(result[58].getBarChartValue(), toBigDecimal(0.4158632736));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(0.4689986793));
        assertEquals(result[72].getSignalLineValue(), toBigDecimal(1.4764845153));
        assertEquals(result[72].getBarChartValue(), toBigDecimal(-1.0074858360));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {PERCENTAGE_PRICE_OSCILLATOR}, size: {0}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[0])
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {PERCENTAGE_PRICE_OSCILLATOR}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(null)
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {PERCENTAGE_PRICE_OSCILLATOR}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void slowPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PERCENTAGE_PRICE_OSCILLATOR}, period: {-12}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(-12)
                .fastPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void fastPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PERCENTAGE_PRICE_OSCILLATOR}, period: {-26}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(12)
                .fastPeriod(-26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void signalPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PERCENTAGE_PRICE_OSCILLATOR}, period: {-9}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(-9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {PERCENTAGE_PRICE_OSCILLATOR}}, movingAverageType: {AVERAGE_TRUE_RANGE}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .build()).getResult();
    }

    @Test
    public void notEnoughDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {PERCENTAGE_PRICE_OSCILLATOR}, period: {35}, size: {34}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[34])
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    private PPORequest buildRequest() {
        return PPORequest.builder()
                .originalData(originalData)
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

}