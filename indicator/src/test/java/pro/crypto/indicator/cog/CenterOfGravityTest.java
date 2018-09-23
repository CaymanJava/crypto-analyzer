package pro.crypto.indicator.cog;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.MOVING_AVERAGE_CONVERGENCE_DIVERGENCE;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class CenterOfGravityTest extends IndicatorAbstractTest {

    @Test
    public void testCenterOfGravityWithPeriodsTen() {
        COGResult[] result = new CenterOfGravity(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineValue());
        assertNull(result[8].getIndicatorValue());
        assertNull(result[8].getSignalLineValue());
        assertEquals(result[9].getTime(), of(2018, 3, 6, 0, 0));
        assertEquals(result[9].getIndicatorValue(), toBigDecimal(5.5124647213));
        assertNull(result[9].getSignalLineValue());
        assertEquals(result[18].getTime(), of(2018, 3, 15, 0, 0));
        assertEquals(result[18].getIndicatorValue(), toBigDecimal(5.4490427889));
        assertEquals(result[18].getSignalLineValue(), toBigDecimal(5.4473202132));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(5.6310637339));
        assertEquals(result[32].getSignalLineValue(), toBigDecimal(5.4974911621));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(5.5608715031));
        assertEquals(result[45].getSignalLineValue(), toBigDecimal(5.5510390031));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(5.4387362339));
        assertEquals(result[72].getSignalLineValue(), toBigDecimal(5.4717574354));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CENTER_OF_GRAVITY}, size: {0}}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(new Tick[0])
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .signalLinePeriod(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CENTER_OF_GRAVITY}}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(null)
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .signalLinePeriod(10)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CENTER_OF_GRAVITY}, period: {20}, size: {19}}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(new Tick[19])
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .signalLinePeriod(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CENTER_OF_GRAVITY}, period: {-10}}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .signalLinePeriod(10)
                .build()).getResult();
    }

    @Test
    public void signalLinePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CENTER_OF_GRAVITY}, period: {-10}}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(new Tick[100])
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .signalLinePeriod(-10)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {CENTER_OF_GRAVITY}}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(new Tick[100])
                .period(10)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .signalLinePeriod(10)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {CENTER_OF_GRAVITY}}, movingAverageType: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}");
        new CenterOfGravity(COGRequest.builder()
                .originalData(new Tick[100])
                .period(10)
                .priceType(CLOSE)
                .movingAverageType(MOVING_AVERAGE_CONVERGENCE_DIVERGENCE)
                .signalLinePeriod(10)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return COGRequest.builder()
                .originalData(originalData)
                .period(10)
                .priceType(CLOSE)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .signalLinePeriod(10)
                .build();
    }

}
