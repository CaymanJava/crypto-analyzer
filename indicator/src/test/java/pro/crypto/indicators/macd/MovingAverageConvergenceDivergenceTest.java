package pro.crypto.indicators.macd;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithClosePriceGenerator;
import pro.crypto.model.request.MACDCreationRequest;
import pro.crypto.model.result.MACDResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.MOVING_AVERAGE_CONVERGENCE_DIVERGENCE;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
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
        MACDResult[] result = new MovingAverageConvergenceDivergence(createRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[0].getSignalLineResult()));
        assertTrue(isNull(result[0].getBarChartValue()));
        assertTrue(isNull(result[24].getIndicatorValue()));
        assertTrue(isNull(result[24].getSignalLineResult()));
        assertTrue(isNull(result[24].getBarChartValue()));
        assertEquals(result[25].getTime(), of(2018, 3, 22, 0, 0));
        assertEquals(result[25].getOriginalValue(), new BigDecimal(7.2200000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[25].getIndicatorValue(), new BigDecimal(0.4056563357).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[25].getSignalLineResult()));
        assertTrue(isNull(result[25].getBarChartValue()));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getOriginalValue(), new BigDecimal(6.5000000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getIndicatorValue(), new BigDecimal(0.2846054137).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[32].getSignalLineResult()));
        assertTrue(isNull(result[32].getBarChartValue()));
        assertEquals(result[33].getTime(), of(2018, 3, 30, 0, 0));
        assertEquals(result[33].getOriginalValue(), new BigDecimal(6.1000000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[33].getIndicatorValue(), new BigDecimal(0.1889216936).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[33].getSignalLineResult(), new BigDecimal(0.3917744361).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[33].getBarChartValue(), new BigDecimal(-0.2028527425).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[38].getTime(), of(2018, 4, 4, 0, 0));
        assertEquals(result[38].getOriginalValue(), new BigDecimal(7.4300000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[38].getIndicatorValue(), new BigDecimal(0.1968265092).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[38].getSignalLineResult(), new BigDecimal(0.2394121086).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[38].getBarChartValue(), new BigDecimal(-0.0425855994).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[44].getTime(), of(2018, 4, 10, 0, 0));
        assertEquals(result[44].getOriginalValue(), new BigDecimal(8.2300000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[44].getIndicatorValue(), new BigDecimal(0.3922420233).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[44].getSignalLineResult(), new BigDecimal(0.2972576828).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[44].getBarChartValue(), new BigDecimal(0.0949843405).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getOriginalValue(), new BigDecimal(6.0500000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[49].getIndicatorValue(), new BigDecimal(0.1824057756).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[49].getSignalLineResult(), new BigDecimal(0.3203577637).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[49].getBarChartValue(), new BigDecimal(-0.1379519881).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, size: {0}}");
        new MovingAverageConvergenceDivergence(MACDCreationRequest.builder()
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
        new MovingAverageConvergenceDivergence(MACDCreationRequest.builder()
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
        new MovingAverageConvergenceDivergence(MACDCreationRequest.builder()
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
        new MovingAverageConvergenceDivergence(MACDCreationRequest.builder()
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
        new MovingAverageConvergenceDivergence(MACDCreationRequest.builder()
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
        new MovingAverageConvergenceDivergence(MACDCreationRequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(12)
                .fastPeriod(26)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptyIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not supported {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, movingAverageType: {null}");
        new MovingAverageConvergenceDivergence(MACDCreationRequest.builder()
                .originalData(new Tick[100])
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not supported {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, movingAverageType: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}");
        new MovingAverageConvergenceDivergence(MACDCreationRequest.builder()
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
        expectedException.expectMessage("Incoming tick data is not enough {indicator: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}, tickLength: {10}, slowPeriod: {12}, signalPeriod: {9}}");
        new MovingAverageConvergenceDivergence(MACDCreationRequest.builder()
                .originalData(new Tick[10])
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    private MACDCreationRequest createRequest() {
        return MACDCreationRequest.builder()
                .originalData(originalData)
                .slowPeriod(12)
                .fastPeriod(26)
                .signalPeriod(9)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

}