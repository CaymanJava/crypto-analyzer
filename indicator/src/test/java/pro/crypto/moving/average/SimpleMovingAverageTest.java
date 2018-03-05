package pro.crypto.moving.average;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.model.result.MovingAverageResult;
import pro.crypto.model.tick.Tick;
import pro.crypto.model.request.MovingAverageCreationRequest;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.PriceType.CLOSE;

public class SimpleMovingAverageTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    private MovingAverageCreationRequest request;

    @Before
    public void init() {
        FifteenMinTickWithClosePriceOnlyGenerator generator = new FifteenMinTickWithClosePriceOnlyGenerator(of(2018, 2, 25, 0, 0));
        originalData = generator.generate();
        request = buildMovingAverageCreationRequest();
    }

    @Test
    public void testWithPeriodThree() throws Exception {
        request.setPeriod(3);
        MovingAverageResult[] result = MovingAverageFactory.createMovingAverage(request).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[1].getIndicatorValue()));
        assertEquals(result[2].getTime(), of(2018, 2, 25, 0, 30));
        assertEquals(result[2].getIndicatorValue(), new BigDecimal(6.5000000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[16].getTime(), of(2018, 2, 25, 4, 0));
        assertEquals(result[16].getIndicatorValue(), new BigDecimal(7.1666666667).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void testWithPeriodFive() throws Exception {
        request.setPeriod(5);
        MovingAverageResult[] result = MovingAverageFactory.createMovingAverage(request).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[3].getIndicatorValue()));
        assertEquals(result[4].getTime(), of(2018, 2, 25, 1, 0));
        assertEquals(result[4].getIndicatorValue(), new BigDecimal(6.7000000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[16].getTime(), of(2018, 2, 25, 4, 0));
        assertEquals(result[16].getIndicatorValue(), new BigDecimal(6.6200000000).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {SIMPLE_MOVING_AVERAGE}, size: {0}}");
        MovingAverageFactory.createMovingAverage(MovingAverageCreationRequest.builder()
                .originalData(new Tick[0])
                .period(5)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {SIMPLE_MOVING_AVERAGE}}");
        MovingAverageFactory.createMovingAverage(MovingAverageCreationRequest.builder()
                .originalData(null)
                .period(5)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {SIMPLE_MOVING_AVERAGE}, period: {5}, size: {1}}");
        MovingAverageFactory.createMovingAverage(MovingAverageCreationRequest.builder()
                .originalData(new Tick[1])
                .period(5)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {SIMPLE_MOVING_AVERAGE}, period: {-5}}");
        MovingAverageFactory.createMovingAverage(MovingAverageCreationRequest.builder()
                .originalData(new Tick[1])
                .period(-5)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {SIMPLE_MOVING_AVERAGE}}");
        MovingAverageFactory.createMovingAverage(MovingAverageCreationRequest.builder()
                .originalData(new Tick[30])
                .period(5)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .priceType(null)
                .build()).getResult();
    }

    private MovingAverageCreationRequest buildMovingAverageCreationRequest() {
        return MovingAverageCreationRequest.builder()
                .originalData(originalData)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

}