package pro.crypto.indicator.atr;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;

public class AverageTrueRangeTest extends IndicatorAbstractTest {

    @Test
    public void testATRWithPeriodTen() {
        ATRResult[] result = new AverageTrueRange(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineValue());
        assertNull(result[8].getIndicatorValue());
        assertNull(result[8].getSignalLineValue());
        assertEquals(result[9].getTime(), of(2018, 3, 6, 0, 0));
        assertEquals(result[9].getIndicatorValue(), toBigDecimal(31.81202));
        assertNull(result[8].getSignalLineValue());
        assertEquals(result[18].getTime(), of(2018, 3, 15, 0, 0));
        assertEquals(result[18].getIndicatorValue(), toBigDecimal(32.6122649243));
        assertEquals(result[18].getSignalLineValue(), toBigDecimal(31.6969915681));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(39.5048683376));
        assertEquals(result[32].getSignalLineValue(), toBigDecimal(36.8699200723));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(37.6810440543));
        assertEquals(result[45].getSignalLineValue(), toBigDecimal(37.7614577575));
        assertEquals(result[64].getTime(), of(2018, 4, 30, 0, 0));
        assertEquals(result[64].getIndicatorValue(), toBigDecimal(35.7453881334));
        assertEquals(result[64].getSignalLineValue(), toBigDecimal(36.4667357356));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(33.2115532806));
        assertEquals(result[72].getSignalLineValue(), toBigDecimal(35.3673677514));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {AVERAGE_TRUE_RANGE}, size: {0}}");
        new AverageTrueRange(ATRRequest.builder()
                .originalData(new Tick[0])
                .period(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {AVERAGE_TRUE_RANGE}}");
        new AverageTrueRange(ATRRequest.builder()
                .originalData(null)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {AVERAGE_TRUE_RANGE}, period: {20}, size: {19}}");
        new AverageTrueRange(ATRRequest.builder()
                .originalData(new Tick[19])
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {AVERAGE_TRUE_RANGE}, period: {-10}}");
        new AverageTrueRange(ATRRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(10)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(10)
                .build();
    }

}
