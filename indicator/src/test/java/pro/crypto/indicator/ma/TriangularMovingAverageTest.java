package pro.crypto.indicator.ma;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.TRIANGULAR_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class TriangularMovingAverageTest extends IndicatorAbstractTest {

    @Test
    public void testTriangularMovingAverageWithEvenPeriod() {
        MARequest request = buildRequest();
        request.setPeriod(14);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[5].getIndicatorValue());
        assertNull(result[12].getIndicatorValue());
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getIndicatorValue(), toBigDecimal(1285.1794607143));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(1229.8676821428));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(1168.6062589286));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(1319.6860839286));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1429.2874839286));
    }

    @Test
    public void testTriangularMovingAverageWithOddPeriod() {
        MARequest request = buildRequest();
        request.setPeriod(13);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[5].getIndicatorValue());
        assertNull(result[11].getIndicatorValue());
        assertEquals(result[12].getTime(), of(2018, 3, 9, 0, 0));
        assertEquals(result[12].getIndicatorValue(), toBigDecimal(1289.2546918367));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(1224.1155163265));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(1169.6895959184));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(1322.2394020408));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1424.1412081633));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {TRIANGULAR_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[0])
                .period(5)
                .indicatorType(TRIANGULAR_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {TRIANGULAR_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(null)
                .period(5)
                .indicatorType(TRIANGULAR_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {TRIANGULAR_MOVING_AVERAGE}, period: {5}, size: {1}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[1])
                .period(5)
                .indicatorType(TRIANGULAR_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {TRIANGULAR_MOVING_AVERAGE}, period: {-5}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[1])
                .period(-5)
                .indicatorType(TRIANGULAR_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {TRIANGULAR_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[30])
                .period(5)
                .indicatorType(TRIANGULAR_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Override
    protected MARequest buildRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .indicatorType(TRIANGULAR_MOVING_AVERAGE)
                .build();
    }

}
