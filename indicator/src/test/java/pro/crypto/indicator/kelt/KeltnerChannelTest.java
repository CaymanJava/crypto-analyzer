package pro.crypto.indicator.kelt;

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
import static pro.crypto.model.tick.PriceType.CLOSE;

public class KeltnerChannelTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testKeltnerChannelWithDefaultParameters() {
        KELTResult[] result = new KeltnerChannel(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertNull(result[0].getBasis());
        assertNull(result[0].getUpperEnvelope());
        assertNull(result[0].getLowerEnvelope());
        assertEquals(result[8].getTime(), of(2018, 3, 5, 0, 0));
        assertNull(result[8].getBasis());
        assertNull(result[8].getUpperEnvelope());
        assertNull(result[8].getLowerEnvelope());
        assertEquals(result[18].getTime(), of(2018, 3, 15, 0, 0));
        assertNull(result[18].getBasis());
        assertNull(result[18].getUpperEnvelope());
        assertNull(result[18].getLowerEnvelope());
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getBasis(), toBigDecimal(1251.06901));
        assertEquals(result[19].getUpperEnvelope(), toBigDecimal(1315.9051068638));
        assertEquals(result[19].getLowerEnvelope(), toBigDecimal(1186.2329131362));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getBasis(), toBigDecimal(1215.1899224323));
        assertEquals(result[32].getUpperEnvelope(), toBigDecimal(1294.1996591075));
        assertEquals(result[32].getLowerEnvelope(), toBigDecimal(1136.1801857571));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getBasis(), toBigDecimal(1306.1636910393));
        assertEquals(result[45].getUpperEnvelope(), toBigDecimal(1381.5257791479));
        assertEquals(result[45].getLowerEnvelope(), toBigDecimal(1230.8016029307));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getBasis(), toBigDecimal(1405.7344873457));
        assertEquals(result[72].getUpperEnvelope(), toBigDecimal(1472.1575939069));
        assertEquals(result[72].getLowerEnvelope(), toBigDecimal(1339.3113807845));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {KELTNER_CHANNEL}, size: {0}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {KELTNER_CHANNEL}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void movingAveragePeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {KELTNER_CHANNEL}, period: {20}, size: {19}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[19])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void averageTrueRangePeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {KELTNER_CHANNEL}, period: {20}, size: {19}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[19])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(10)
                .averageTrueRangePeriod(20)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void averageTrueRangeShiftLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Shift should be more or equals 0 {indicator: {KELTNER_CHANNEL}, shift: {-2.00}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(-2)
                .build()).getResult();
    }

    @Test
    public void movingAveragePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KELTNER_CHANNEL}, period: {-20}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(-20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void averageTrueRangePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KELTNER_CHANNEL}, period: {-10}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(-10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {KELTNER_CHANNEL}}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {KELTNER_CHANNEL}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new KeltnerChannel(KELTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build()).getResult();
    }

    private IndicatorRequest buildRequest() {
        return KELTRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .averageTrueRangePeriod(10)
                .averageTrueRangeShift(2)
                .build();
    }

}