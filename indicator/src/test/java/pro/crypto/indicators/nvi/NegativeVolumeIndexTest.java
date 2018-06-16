package pro.crypto.indicators.nvi;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.NVIRequest;
import pro.crypto.model.result.NVIResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class NegativeVolumeIndexTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testNegativeVolumeIndexWithPeriodTwentyFive() {
        NVIResult[] result = new NegativeVolumeIndex(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getIndicatorValue(), toBigDecimal(15.5471));
        assertNull(result[0].getMovingAverageValue());
        assertEquals(result[23].getTime(), of(2018, 3, 20, 0, 0));
        assertEquals(result[23].getIndicatorValue(), toBigDecimal(15.771228981));
        assertNull(result[23].getMovingAverageValue());
        assertEquals(result[24].getTime(), of(2018, 3, 21, 0, 0));
        assertEquals(result[24].getIndicatorValue(), toBigDecimal(15.771228981));
        assertEquals(result[24].getMovingAverageValue(), toBigDecimal(15.5202395458));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(17.0890726644));
        assertEquals(result[32].getMovingAverageValue(), toBigDecimal(15.9579241089));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(17.892728641));
        assertEquals(result[45].getMovingAverageValue(), toBigDecimal(16.8646577814));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(17.1064192833));
        assertEquals(result[72].getMovingAverageValue(), toBigDecimal(17.8345727616));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {NEGATIVE_VOLUME_INDEX}, size: {0}}");
        new NegativeVolumeIndex(NVIRequest.builder()
                .originalData(new Tick[0])
                .period(25)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {NEGATIVE_VOLUME_INDEX}}");
        new NegativeVolumeIndex(NVIRequest.builder()
                .originalData(null)
                .period(25)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {NEGATIVE_VOLUME_INDEX}, period: {25}, size: {24}}");
        new NegativeVolumeIndex(NVIRequest.builder()
                .originalData(new Tick[24])
                .period(25)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {NEGATIVE_VOLUME_INDEX}, period: {-25}}");
        new NegativeVolumeIndex(NVIRequest.builder()
                .originalData(new Tick[100])
                .period(-25)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {NEGATIVE_VOLUME_INDEX}}");
        new NegativeVolumeIndex(NVIRequest.builder()
                .originalData(new Tick[100])
                .period(25)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {NEGATIVE_VOLUME_INDEX}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new NegativeVolumeIndex(NVIRequest.builder()
                .originalData(new Tick[100])
                .period(25)
                .priceType(CLOSE)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .build()).getResult();
    }

    private NVIRequest buildRequest() {
        return NVIRequest.builder()
                .originalData(originalData)
                .period(25)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

}