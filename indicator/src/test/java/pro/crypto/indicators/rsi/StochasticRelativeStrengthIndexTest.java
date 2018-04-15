package pro.crypto.indicators.rsi;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.StochRSIRequest;
import pro.crypto.model.result.RSIResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.*;

public class StochasticRelativeStrengthIndexTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testStochasticRelativeStrengthIndexWithExponentialMovingAverage() {
        RSIResult[] result = new StochasticRelativeStrengthIndex(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[5].getIndicatorValue()));
        assertTrue(isNull(result[12].getIndicatorValue()));
        assertTrue(isNull(result[25].getIndicatorValue()));
        assertEquals(result[26].getTime(), of(2018, 3, 23, 0, 0));
        assertEquals(result[26].getIndicatorValue(), new BigDecimal(0.0568788623).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), new BigDecimal(0.8180305509).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), new BigDecimal(1.0000000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[66].getTime(), of(2018, 5, 2, 0, 0));
        assertEquals(result[66].getIndicatorValue(), new BigDecimal(0.0000000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), new BigDecimal(0.0381200833).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {STOCHASTIC_RELATIVE_STRENGTH_INDEX}, size: {0}}");
        new StochasticRelativeStrengthIndex(StochRSIRequest.builder()
                .originalData(new Tick[0])
                .movingAverageType(SMOOTHED_MOVING_AVERAGE)
                .rsiPeriod(14)
                .stochPeriod(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {STOCHASTIC_RELATIVE_STRENGTH_INDEX}}");
        new StochasticRelativeStrengthIndex(StochRSIRequest.builder()
                .originalData(null)
                .movingAverageType(SMOOTHED_MOVING_AVERAGE)
                .rsiPeriod(14)
                .stochPeriod(14)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {STOCHASTIC_RELATIVE_STRENGTH_INDEX}, period: {34}, size: {33}}");
        new StochasticRelativeStrengthIndex(StochRSIRequest.builder()
                .originalData(new Tick[33])
                .movingAverageType(SMOOTHED_MOVING_AVERAGE)
                .rsiPeriod(20)
                .stochPeriod(14)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {STOCHASTIC_RELATIVE_STRENGTH_INDEX}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new StochasticRelativeStrengthIndex(StochRSIRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .rsiPeriod(14)
                .stochPeriod(14)
                .build()).getResult();
    }

    private StochRSIRequest buildRequest() {
        return StochRSIRequest.builder()
                .originalData(originalData)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .rsiPeriod(14)
                .stochPeriod(14)
                .build();
    }

}