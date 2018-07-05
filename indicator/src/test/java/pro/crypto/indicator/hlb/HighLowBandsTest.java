package pro.crypto.indicator.hlb;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class HighLowBandsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testHighLowBandsWithPeriodThirteenAndFivePercentageShift() {
        HLBResult[] result = new HighLowBands(buildRequest(13, 5)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getBasis());
        assertNull(result[0].getUpperEnvelope());
        assertNull(result[0].getLowerEnvelope());
        assertNull(result[11].getBasis());
        assertNull(result[11].getUpperEnvelope());
        assertNull(result[11].getLowerEnvelope());
        assertEquals(result[12].getTime(), of(2018, 3, 9, 0, 0));
        assertEquals(result[12].getBasis(), toBigDecimal(1289.2546918367));
        assertEquals(result[12].getUpperEnvelope(), toBigDecimal(1353.7174264287));
        assertEquals(result[12].getLowerEnvelope(), toBigDecimal(1224.7919572447));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getBasis(), toBigDecimal(1169.6895959184));
        assertEquals(result[32].getUpperEnvelope(), toBigDecimal(1228.1740757144));
        assertEquals(result[32].getLowerEnvelope(), toBigDecimal(1111.2051161224));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getBasis(), toBigDecimal(1322.2394020408));
        assertEquals(result[45].getUpperEnvelope(), toBigDecimal(1388.3513721428));
        assertEquals(result[45].getLowerEnvelope(), toBigDecimal(1256.1274319388));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getBasis(), toBigDecimal(1424.1412081633));
        assertEquals(result[72].getUpperEnvelope(), toBigDecimal(1495.3482685713));
        assertEquals(result[72].getLowerEnvelope(), toBigDecimal(1352.9341477553));
    }

    @Test
    public void testHighLowBandsWithPeriodFourteenAndFourPercentageShift() {
        HLBResult[] result = new HighLowBands(buildRequest(14, 4)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getBasis());
        assertNull(result[0].getUpperEnvelope());
        assertNull(result[0].getLowerEnvelope());
        assertNull(result[12].getBasis());
        assertNull(result[12].getUpperEnvelope());
        assertNull(result[12].getLowerEnvelope());
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getBasis(), toBigDecimal(1285.1794607143));
        assertEquals(result[13].getUpperEnvelope(), toBigDecimal(1336.5866391427));
        assertEquals(result[13].getLowerEnvelope(), toBigDecimal(1233.7722822859));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getBasis(), toBigDecimal(1168.6062589286));
        assertEquals(result[32].getUpperEnvelope(), toBigDecimal(1215.3505092858));
        assertEquals(result[32].getLowerEnvelope(), toBigDecimal(1121.8620085714));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getBasis(), toBigDecimal(1319.6860839286));
        assertEquals(result[45].getUpperEnvelope(), toBigDecimal(1372.4735272858));
        assertEquals(result[45].getLowerEnvelope(), toBigDecimal(1266.8986405714));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getBasis(), toBigDecimal(1429.2874839286));
        assertEquals(result[72].getUpperEnvelope(), toBigDecimal(1486.4589832858));
        assertEquals(result[72].getLowerEnvelope(), toBigDecimal(1372.1159845714));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {HIGH_LOW_BANDS}, size: {0}}");
        new HighLowBands(HLBRequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .period(14)
                .shiftPercentage(4)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {HIGH_LOW_BANDS}}");
        new HighLowBands(HLBRequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .period(14)
                .shiftPercentage(4)
                .build()).getResult();
    }

    @Test
    public void shiftPercentageLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Shift should be more or equals 0 {indicator: {HIGH_LOW_BANDS}, shift: {-4.00}}");
        new HighLowBands(HLBRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(14)
                .shiftPercentage(-4)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {HIGH_LOW_BANDS}, period: {14}, size: {13}}");
        new HighLowBands(HLBRequest.builder()
                .originalData(new Tick[13])
                .priceType(CLOSE)
                .period(14)
                .shiftPercentage(4)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {HIGH_LOW_BANDS}, period: {-14}}");
        new HighLowBands(HLBRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .period(-14)
                .shiftPercentage(4)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {HIGH_LOW_BANDS}}");
        new HighLowBands(HLBRequest.builder()
                .originalData(new Tick[100])
                .period(14)
                .shiftPercentage(4)
                .build()).getResult();
    }

    private HLBRequest buildRequest(int period, double shiftPercentage) {
        return HLBRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .period(period)
                .shiftPercentage(shiftPercentage)
                .build();
    }

}