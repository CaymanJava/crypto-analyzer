package pro.crypto.indicators.ma;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.TIME_SERIES_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;
import static pro.crypto.model.tick.PriceType.OPEN;

public class TimeSeriesMovingAverageTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testTimeSeriesMovingAverageWithPeriodFifteen() {
        MAResult[] result = MovingAverageFactory.create(buildRequest(15)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[13].getIndicatorValue());
        assertEquals(result[14].getTime(), of(2018, 3, 11, 0, 0));
        assertEquals(result[14].getIndicatorValue(), toBigDecimal(1229.8321057144));
        assertEquals(result[26].getTime(), of(2018, 3, 23, 0, 0));
        assertEquals(result[26].getIndicatorValue(), toBigDecimal(1170.9277142856));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(1228.66348));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(1331.13914));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(1433.1417085712));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1430.1197085712));
    }

    @Test
    public void testTimeSeriesMovingAverageWithPeriodTwenty() {
        MAResult[] result = MovingAverageFactory.create(buildRequest(20)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[18].getIndicatorValue());
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(1238.3043552629));
        assertEquals(result[26].getTime(), of(2018, 3, 23, 0, 0));
        assertEquals(result[26].getIndicatorValue(), toBigDecimal(1201.670184474));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(1258.40271815765));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(1285.0520910529));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(1406.58474473685));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1386.79497368455));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {TIME_SERIES_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(TIME_SERIES_MOVING_AVERAGE)
                .originalData(new Tick[0])
                .period(5)
                .priceType(OPEN)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {TIME_SERIES_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(TIME_SERIES_MOVING_AVERAGE)
                .originalData(null)
                .period(5)
                .priceType(OPEN)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {TIME_SERIES_MOVING_AVERAGE}, period: {20}, size: {19}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(TIME_SERIES_MOVING_AVERAGE)
                .originalData(new Tick[19])
                .period(20)
                .priceType(OPEN)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {TIME_SERIES_MOVING_AVERAGE}, period: {-5}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(TIME_SERIES_MOVING_AVERAGE)
                .originalData(new Tick[100])
                .period(-5)
                .priceType(OPEN)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {TIME_SERIES_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(TIME_SERIES_MOVING_AVERAGE)
                .originalData(new Tick[100])
                .period(5)
                .build()).getResult();
    }

    private MARequest buildRequest(int period) {
        return MARequest.builder()
                .originalData(originalData)
                .period(period)
                .priceType(CLOSE)
                .indicatorType(TIME_SERIES_MOVING_AVERAGE)
                .build();
    }

}