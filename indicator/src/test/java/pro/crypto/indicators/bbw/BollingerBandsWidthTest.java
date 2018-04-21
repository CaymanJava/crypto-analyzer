package pro.crypto.indicators.bbw;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.BBWRequest;
import pro.crypto.model.result.BBWResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.KELTNER_CHANNEL;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class BollingerBandsWidthTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testBollingerBandsWidthWithDefaultParameters() {
        BBWResult[] result = new BollingerBandsWidth(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertTrue(isNull(result[10].getIndicatorValue()));
        assertTrue(isNull(result[18].getIndicatorValue()));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), new BigDecimal(0.1440312856).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), new BigDecimal(0.1511931961).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), new BigDecimal(0.2164965395).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), new BigDecimal(0.1123171990).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {BOLLINGER_BANDS_WIDTH}, size: {0}}");
        new BollingerBandsWidth(BBWRequest.builder()
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
        expectedException.expectMessage("Incoming tick data is null {indicator: {BOLLINGER_BANDS_WIDTH}}");
        new BollingerBandsWidth(BBWRequest.builder()
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
        expectedException.expectMessage("Period should be less than tick data size {indicator: {BOLLINGER_BANDS_WIDTH}, period: {20}, size: {19}}");
        new BollingerBandsWidth(BBWRequest.builder()
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
        expectedException.expectMessage("Period should be more than 0 {indicator: {BOLLINGER_BANDS_WIDTH}, period: {-10}}");
        new BollingerBandsWidth(BBWRequest.builder()
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
                "{indicator: {BOLLINGER_BANDS_WIDTH}, standardDeviationCoefficient: {-2}}");
        new BollingerBandsWidth(BBWRequest.builder()
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
        expectedException.expectMessage("Incoming price type is null {indicator: {BOLLINGER_BANDS_WIDTH}}");
        new BollingerBandsWidth(BBWRequest.builder()
                .originalData(new Tick[100])
                .period(20)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {BOLLINGER_BANDS_WIDTH}}," +
                " movingAverageType: {KELTNER_CHANNEL}");
        new BollingerBandsWidth(BBWRequest.builder()
                .originalData(new Tick[100])
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(KELTNER_CHANNEL)
                .build()).getResult();
    }

    private BBWRequest buildRequest() {
        return BBWRequest.builder()
                .originalData(originalData)
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build();
    }

}