package pro.crypto.indicator.atrb;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class AverageTrueRangeBandsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAverageTrueRangeBandsWithShiftThree() {
        ATRBResult[] result = new AverageTrueRangeBands(buildRequest(3)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getUpperBand());
        assertNull(result[0].getMiddleBand());
        assertNull(result[0].getLowerBand());
        assertNull(result[3].getUpperBand());
        assertNull(result[3].getMiddleBand());
        assertNull(result[3].getLowerBand());
        assertEquals(result[4].getTime(), of(2018, 3, 1, 0, 0));
        assertEquals(result[4].getUpperBand(), toBigDecimal(1418.282));
        assertEquals(result[4].getMiddleBand(), toBigDecimal(1304.6));
        assertEquals(result[4].getLowerBand(), toBigDecimal(1190.918));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getUpperBand(), toBigDecimal(1270.4683297416));
        assertEquals(result[19].getMiddleBand(), toBigDecimal(1172.0601));
        assertEquals(result[19].getLowerBand(), toBigDecimal(1073.6518702584));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getUpperBand(), toBigDecimal(1398.69377858));
        assertEquals(result[32].getMiddleBand(), toBigDecimal(1272.29));
        assertEquals(result[32].getLowerBand(), toBigDecimal(1145.88622142));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getUpperBand(), toBigDecimal(1513.7373087742));
        assertEquals(result[45].getMiddleBand(), toBigDecimal(1401.16));
        assertEquals(result[45].getLowerBand(), toBigDecimal(1288.5826912258));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getUpperBand(), toBigDecimal(1455.2670945928));
        assertEquals(result[72].getMiddleBand(), toBigDecimal(1362.61));
        assertEquals(result[72].getLowerBand(), toBigDecimal(1269.9529054072));
    }

    @Test
    public void testAverageTrueRangeBandsWithShiftOne() {
        ATRBResult[] result = new AverageTrueRangeBands(buildRequest(1)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getUpperBand());
        assertNull(result[0].getMiddleBand());
        assertNull(result[0].getLowerBand());
        assertNull(result[3].getUpperBand());
        assertNull(result[3].getMiddleBand());
        assertNull(result[3].getLowerBand());
        assertEquals(result[4].getTime(), of(2018, 3, 1, 0, 0));
        assertEquals(result[4].getUpperBand(), toBigDecimal(1342.494));
        assertEquals(result[4].getMiddleBand(), toBigDecimal(1304.6));
        assertEquals(result[4].getLowerBand(), toBigDecimal(1266.706));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getUpperBand(), toBigDecimal(1204.8628432472));
        assertEquals(result[19].getMiddleBand(), toBigDecimal(1172.0601));
        assertEquals(result[19].getLowerBand(), toBigDecimal(1139.2573567528));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getUpperBand(), toBigDecimal(1314.42459286));
        assertEquals(result[32].getMiddleBand(), toBigDecimal(1272.29));
        assertEquals(result[32].getLowerBand(), toBigDecimal(1230.15540714));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getUpperBand(), toBigDecimal(1438.6857695914));
        assertEquals(result[45].getMiddleBand(), toBigDecimal(1401.16));
        assertEquals(result[45].getLowerBand(), toBigDecimal(1363.6342304086));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getUpperBand(), toBigDecimal(1393.4956981976));
        assertEquals(result[72].getMiddleBand(), toBigDecimal(1362.61));
        assertEquals(result[72].getLowerBand(), toBigDecimal(1331.7243018024));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {AVERAGE_TRUE_RANGE_BANDS}, size: {0}}");
        new AverageTrueRangeBands(ATRBRequest.builder()
                .originalData(new Tick[0])
                .period(5)
                .shift(3)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {AVERAGE_TRUE_RANGE_BANDS}}");
        new AverageTrueRangeBands(ATRBRequest.builder()
                .originalData(null)
                .period(5)
                .shift(3)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {AVERAGE_TRUE_RANGE_BANDS}, period: {5}, size: {4}}");
        new AverageTrueRangeBands(ATRBRequest.builder()
                .originalData(new Tick[4])
                .period(5)
                .shift(3)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {AVERAGE_TRUE_RANGE_BANDS}, period: {-5}}");
        new AverageTrueRangeBands(ATRBRequest.builder()
                .originalData(new Tick[100])
                .period(-5)
                .shift(3)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void shiftLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Shift should be more or equals 0 {indicator: {AVERAGE_TRUE_RANGE_BANDS}, shift: {-3.00}}");
        new AverageTrueRangeBands(ATRBRequest.builder()
                .originalData(new Tick[100])
                .period(5)
                .shift(-3.0)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {AVERAGE_TRUE_RANGE_BANDS}}");
        new AverageTrueRangeBands(ATRBRequest.builder()
                .originalData(new Tick[100])
                .period(5)
                .shift(3)
                .build()).getResult();
    }

    private IndicatorRequest buildRequest(double shift) {
        return ATRBRequest.builder()
                .originalData(originalData)
                .period(5)
                .shift(shift)
                .priceType(CLOSE)
                .build();
    }

}