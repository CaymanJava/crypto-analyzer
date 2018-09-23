package pro.crypto.indicator.ma;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;
import static pro.crypto.model.tick.PriceType.OPEN;

public class ExponentialMovingAverageTest extends IndicatorAbstractTest {

    @Test
    public void testExponentialMovingAverageWithPeriodFifteen() {
        MARequest request = buildRequest();
        request.setPeriod(15);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[13].getIndicatorValue());
        assertEquals(result[14].getTime(), of(2018, 3, 11, 0, 0));
        assertEquals(result[14].getIndicatorValue(), toBigDecimal(1268.2726733333));
        assertEquals(result[24].getTime(), of(2018, 3, 21, 0, 0));
        assertEquals(result[24].getIndicatorValue(), toBigDecimal(1197.4686831547));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(1202.7548963946));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1296.7907842725));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(1383.3163937316));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(1423.0841464598));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1407.7117762817));
    }

    @Test
    public void testExponentialMovingAverageWithPeriodTwenty() {
        MARequest request = buildRequest();
        request.setPeriod(20);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[18].getIndicatorValue());
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(1251.06901));
        assertEquals(result[24].getTime(), of(2018, 3, 21, 0, 0));
        assertEquals(result[24].getIndicatorValue(), toBigDecimal(1215.402633243));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(1209.1793879542));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1285.5981932183));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(1369.1583758242));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(1415.2948145949));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1405.7344873457));
    }

    @Test
    public void testExponentialMovingAverageWithAlphaCoefficient() {
        MARequest request = buildRequest();
        request.setPeriod(15);
        request.setAlphaCoefficient(toBigDecimal(0.4));
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[13].getIndicatorValue());
        assertEquals(result[14].getTime(), of(2018, 3, 11, 0, 0));
        assertEquals(result[14].getIndicatorValue(), toBigDecimal(1268.2726733333));
        assertEquals(result[24].getTime(), of(2018, 3, 21, 0, 0));
        assertEquals(result[24].getIndicatorValue(), toBigDecimal(1147.8394486356));
        assertEquals(result[31].getTime(), of(2018, 3, 28, 0, 0));
        assertEquals(result[31].getIndicatorValue(), toBigDecimal(1229.2628093333));
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getIndicatorValue(), toBigDecimal(1337.0746781386));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(1426.3147653061));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(1404.9323063091));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1384.2893068977));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {EXPONENTIAL_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(new Tick[0])
                .period(5)
                .priceType(OPEN)
                .alphaCoefficient(toBigDecimal(0.4))
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {EXPONENTIAL_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(null)
                .period(5)
                .priceType(OPEN)
                .alphaCoefficient(toBigDecimal(0.4))
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {EXPONENTIAL_MOVING_AVERAGE}, period: {5}, size: {1}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(new Tick[1])
                .period(5)
                .priceType(OPEN)
                .alphaCoefficient(toBigDecimal(0.4))
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {EXPONENTIAL_MOVING_AVERAGE}, period: {-5}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(new Tick[1])
                .period(-5)
                .priceType(OPEN)
                .alphaCoefficient(toBigDecimal(0.4))
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {EXPONENTIAL_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(new Tick[30])
                .period(5)
                .priceType(null)
                .alphaCoefficient(toBigDecimal(0.4))
                .build()).getResult();
    }

    @Override
    protected MARequest buildRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

}
