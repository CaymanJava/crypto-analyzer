package pro.crypto.indicators.trix;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.TRIXRequest;
import pro.crypto.model.result.TRIXResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TripleExponentialMovingAverageTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testTRIXWithPeriodEighteen() {
        TRIXResult[] result = new TripleExponentialMovingAverage(buildTRIXRequest(18)).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[0].getSignalLineResult()));
        assertTrue(isNull(result[8].getIndicatorValue()));
        assertTrue(isNull(result[8].getSignalLineResult()));
        assertTrue(isNull(result[51].getIndicatorValue()));
        assertTrue(isNull(result[51].getSignalLineResult()));
        assertEquals(result[52].getTime(), of(2018, 4, 18, 0, 0));
        assertEquals(result[52].getIndicatorValue(), new BigDecimal(0.4083115144).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[52].getSignalLineResult()));
        assertEquals(result[59].getTime(), of(2018, 4, 25, 0, 0));
        assertEquals(result[59].getIndicatorValue(), new BigDecimal(0.4202200915).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[59].getSignalLineResult()));
        assertEquals(result[60].getTime(), of(2018, 4, 26, 0, 0));
        assertEquals(result[60].getIndicatorValue(), new BigDecimal(0.4267473783).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[60].getSignalLineResult(), new BigDecimal(0.4103691603).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[65].getTime(), of(2018, 5, 1, 0, 0));
        assertEquals(result[65].getIndicatorValue(), new BigDecimal(0.4474297471).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[65].getSignalLineResult(), new BigDecimal(0.4344003122).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), new BigDecimal(0.3042856299).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getSignalLineResult(), new BigDecimal(0.3738696705).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void testTRIXWithPeriodFourteen() {
        TRIXResult[] result = new TripleExponentialMovingAverage(buildTRIXRequest(14)).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[0].getSignalLineResult()));
        assertTrue(isNull(result[8].getIndicatorValue()));
        assertTrue(isNull(result[8].getSignalLineResult()));
        assertTrue(isNull(result[39].getIndicatorValue()));
        assertTrue(isNull(result[39].getSignalLineResult()));
        assertEquals(result[40].getTime(), of(2018, 4, 6, 0, 0));
        assertEquals(result[40].getIndicatorValue(), new BigDecimal(0.2736429115).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[40].getSignalLineResult()));
        assertEquals(result[47].getTime(), of(2018, 4, 13, 0, 0));
        assertEquals(result[47].getIndicatorValue(), new BigDecimal(0.4608041176).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[47].getSignalLineResult()));
        assertEquals(result[48].getTime(), of(2018, 4, 14, 0, 0));
        assertEquals(result[48].getIndicatorValue(), new BigDecimal(0.4736482148).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[48].getSignalLineResult(), new BigDecimal(0.3745135872).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[65].getTime(), of(2018, 5, 1, 0, 0));
        assertEquals(result[65].getIndicatorValue(), new BigDecimal(0.4747801761).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[65].getSignalLineResult(), new BigDecimal(0.4669545161).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), new BigDecimal(0.2178900988).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getSignalLineResult(), new BigDecimal(0.3398574781).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}, size: {0}}");
        new TripleExponentialMovingAverage(TRIXRequest.builder()
                .originalData(new Tick[0])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}}");
        new TripleExponentialMovingAverage(TRIXRequest.builder()
                .originalData(null)
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}," +
                " period: {70}, size: {69}}");
        new TripleExponentialMovingAverage(TRIXRequest.builder()
                .originalData(new Tick[69])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {TRIPLE_EXPONENTIAL_MOVING_AVERAGE}, period: {-14}");
        new TripleExponentialMovingAverage(TRIXRequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .build()).getResult();
    }

    private TRIXRequest buildTRIXRequest(int period) {
        return TRIXRequest.builder()
                .originalData(originalData)
                .period(period)
                .build();
    }

}