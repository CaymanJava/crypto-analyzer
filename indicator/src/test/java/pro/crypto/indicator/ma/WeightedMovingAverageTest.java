package pro.crypto.indicator.ma;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.WEIGHTED_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class WeightedMovingAverageTest extends IndicatorAbstractTest {

    @Test
    public void testWeightedMovingAverageWithPeriodFifteen() {
        MARequest request = buildRequest();
        request.setPeriod(15);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[13].getIndicatorValue());
        assertEquals(result[14].getTime(), of(2018, 3, 11, 0, 0));
        assertEquals(result[14].getIndicatorValue(), toBigDecimal(1255.176255));
        assertEquals(result[24].getTime(), of(2018, 3, 21, 0, 0));
        assertEquals(result[24].getIndicatorValue(), toBigDecimal(1180.4837825));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(1189.9120833333));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1315.2931741667));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(1395.5807591667));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(1437.8453175));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1412.029655));
    }

    @Test
    public void testWeightedMovingAveragePeriodTwenty() {
        MARequest request = buildRequest();
        request.setPeriod(20);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[18].getIndicatorValue());
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(1231.0905838095));
        assertEquals(result[24].getTime(), of(2018, 3, 21, 0, 0));
        assertEquals(result[24].getIndicatorValue(), toBigDecimal(1194.1985019048));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(1189.04453));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1298.4248138095));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(1387.5981038095));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(1434.5342252381));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1419.8086090476));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {WEIGHTED_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[0])
                .period(5)
                .indicatorType(WEIGHTED_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {WEIGHTED_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(null)
                .period(5)
                .indicatorType(WEIGHTED_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {WEIGHTED_MOVING_AVERAGE}, period: {5}, size: {1}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[1])
                .period(5)
                .indicatorType(WEIGHTED_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {WEIGHTED_MOVING_AVERAGE}, period: {-5}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[1])
                .period(-5)
                .indicatorType(WEIGHTED_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {WEIGHTED_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[30])
                .period(5)
                .indicatorType(WEIGHTED_MOVING_AVERAGE)
                .priceType(null)
                .build()).getResult();
    }

    @Override
    protected MARequest buildRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .indicatorType(WEIGHTED_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

}
