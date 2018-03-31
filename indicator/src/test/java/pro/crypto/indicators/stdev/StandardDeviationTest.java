package pro.crypto.indicators.stdev;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.StDevRequest;
import pro.crypto.model.result.StDevResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.MOVING_AVERAGE_CONVERGENCE_DIVERGENCE;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class StandardDeviationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testStDevWithDefaultParamsAndPeriodFourteen() {
        StDevResult[] result = new StandardDeviation(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[10].getIndicatorValue()));
        assertTrue(isNull(result[12].getIndicatorValue()));
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getIndicatorValue(), new BigDecimal(30.8512831603).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getIndicatorValue(), new BigDecimal(36.1612276578).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), new BigDecimal(51.7657535697).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), new BigDecimal(36.9170102022).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), new BigDecimal(41.8270946851).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {STANDARD_DEVIATION}, size: {0}}");
        new StandardDeviation(StDevRequest.builder()
                .originalData(new Tick[0])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {STANDARD_DEVIATION}}");
        new StandardDeviation(StDevRequest.builder()
                .originalData(null)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {STANDARD_DEVIATION}, period: {20}, size: {10}}");
        new StandardDeviation(StDevRequest.builder()
                .originalData(new Tick[10])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .period(20)
                .build()).getResult();
    }

    @Test
    public void shortPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {STANDARD_DEVIATION}, period: {-20}}");
        new StandardDeviation(StDevRequest.builder()
                .originalData(new Tick[10])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .period(-20)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {STANDARD_DEVIATION}}");
        new StandardDeviation(StDevRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {STANDARD_DEVIATION}}, movingAverageType: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}");
        new StandardDeviation(StDevRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(MOVING_AVERAGE_CONVERGENCE_DIVERGENCE)
                .priceType(CLOSE)
                .period(14)
                .build()).getResult();
    }


    private StDevRequest buildRequest() {
        return StDevRequest.builder()
                .originalData(originalData)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .period(14)
                .build();
    }

}