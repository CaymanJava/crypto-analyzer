package pro.crypto.indicators.bb;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.BBRequest;
import pro.crypto.model.result.BBResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.KELTNER_CHANNEL;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class BollingerBandsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testBollingerBandsWithDefaultParameters() {
        BBResult[] result = new BollingerBands(buildBBRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getUpperBand()));
        assertTrue(isNull(result[0].getMiddleBand()));
        assertTrue(isNull(result[0].getLowerBand()));
        assertTrue(isNull(result[8].getUpperBand()));
        assertTrue(isNull(result[8].getMiddleBand()));
        assertTrue(isNull(result[8].getLowerBand()));
        assertTrue(isNull(result[18].getUpperBand()));
        assertTrue(isNull(result[18].getMiddleBand()));
        assertTrue(isNull(result[18].getLowerBand()));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getUpperBand(), new BigDecimal(1341.1655489174).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[19].getMiddleBand(), new BigDecimal(1251.0690100000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[19].getLowerBand(), new BigDecimal(1160.9724710826).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getUpperBand(), new BigDecimal(1282.8662298336).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getMiddleBand(), new BigDecimal(1192.7020150000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getLowerBand(), new BigDecimal(1102.5378001664).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getUpperBand(), new BigDecimal(1427.8260655722).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getMiddleBand(), new BigDecimal(1288.3630000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getLowerBand(), new BigDecimal(1148.8999344278).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getUpperBand(), new BigDecimal(1507.2465671536).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getMiddleBand(), new BigDecimal(1427.1024900000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getLowerBand(), new BigDecimal(1346.9584128464).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {BOLLINGER_BANDS}, size: {0}}");
        new BollingerBands(BBRequest.builder()
                .originalData(new Tick[0])
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {BOLLINGER_BANDS}}");
        new BollingerBands(BBRequest.builder()
                .originalData(null)
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {BOLLINGER_BANDS}, period: {20}, size: {19}}");
        new BollingerBands(BBRequest.builder()
                .originalData(new Tick[19])
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {BOLLINGER_BANDS}, period: {-10}}");
        new BollingerBands(BBRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void coefficientLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Standard Deviation Coefficient should be more than 1 " +
                "{indicator: {BOLLINGER_BANDS}, standardDeviationCoefficient: {-2}}");
        new BollingerBands(BBRequest.builder()
                .originalData(new Tick[100])
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(-2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {BOLLINGER_BANDS}}");
        new BollingerBands(BBRequest.builder()
                .originalData(new Tick[100])
                .period(20)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {BOLLINGER_BANDS}}," +
                " movingAverageType: {KELTNER_CHANNEL}");
        new BollingerBands(BBRequest.builder()
                .originalData(new Tick[100])
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(KELTNER_CHANNEL)
                .build()).getResult();
    }

    private BBRequest buildBBRequest() {
        return BBRequest.builder()
                .originalData(originalData)
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build();
    }

}