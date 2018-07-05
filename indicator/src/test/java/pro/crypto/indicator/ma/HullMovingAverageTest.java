package pro.crypto.indicator.ma;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.FifteenMinTickWithClosePriceOnlyGenerator;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.HULL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class HullMovingAverageTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    private MARequest request;

    @Before
    public void init() {
        FifteenMinTickWithClosePriceOnlyGenerator generator = new FifteenMinTickWithClosePriceOnlyGenerator(of(2018, 2, 25, 0, 0));
        originalData = generator.generate();
        request = buildMovingAverageCreationRequest();
    }

    @Test
    public void testWithPeriodFour() throws Exception {
        request.setPeriod(4);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[2].getIndicatorValue());
        assertEquals(result[3].getTime(), of(2018, 2, 25, 0, 45));
        assertEquals(result[3].getIndicatorValue(), toBigDecimal(7.65));
        assertEquals(result[16].getTime(), of(2018, 2, 25, 4, 0));
        assertEquals(result[16].getIndicatorValue(), toBigDecimal(7.875));
    }

    @Test
    public void testWithPeriodFive() throws Exception {
        request.setPeriod(5);
        MAResult[] result = MovingAverageFactory.create(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[3].getIndicatorValue());
        assertEquals(result[4].getTime(), of(2018, 2, 25, 1, 0));
        assertEquals(result[4].getIndicatorValue(), toBigDecimal(7.3));
        assertEquals(result[16].getTime(), of(2018, 2, 25, 4, 0));
        assertEquals(result[16].getIndicatorValue(), toBigDecimal(7.88));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {HULL_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[0])
                .period(5)
                .indicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {HULL_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(null)
                .period(5)
                .indicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {HULL_MOVING_AVERAGE}, period: {5}, size: {1}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[1])
                .period(5)
                .indicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {HULL_MOVING_AVERAGE}, period: {-5}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[1])
                .period(-5)
                .indicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {HULL_MOVING_AVERAGE}}");
        MovingAverageFactory.create(MARequest.builder()
                .originalData(new Tick[30])
                .period(5)
                .indicatorType(HULL_MOVING_AVERAGE)
                .priceType(null)
                .build()).getResult();
    }

    private MARequest buildMovingAverageCreationRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .indicatorType(HULL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

}