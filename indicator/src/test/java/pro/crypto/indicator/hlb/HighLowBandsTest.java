package pro.crypto.indicator.hlb;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class HighLowBandsTest extends IndicatorAbstractTest {

    @Test
    public void testHighLowBandsWithPeriodThirteenAndFivePercentageShift() {
        HLBRequest request = buildRequest();
        request.setPeriod(13);
        request.setShiftPercentage(5);
        HLBResult[] result = new HighLowBands(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getMiddleBand());
        assertNull(result[0].getUpperBand());
        assertNull(result[0].getLowerBand());
        assertNull(result[11].getMiddleBand());
        assertNull(result[11].getUpperBand());
        assertNull(result[11].getLowerBand());
        assertEquals(result[12].getTime(), of(2018, 3, 9, 0, 0));
        assertEquals(result[12].getMiddleBand(), toBigDecimal(1289.2546918367));
        assertEquals(result[12].getUpperBand(), toBigDecimal(1353.7174264287));
        assertEquals(result[12].getLowerBand(), toBigDecimal(1224.7919572447));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getMiddleBand(), toBigDecimal(1169.6895959184));
        assertEquals(result[32].getUpperBand(), toBigDecimal(1228.1740757144));
        assertEquals(result[32].getLowerBand(), toBigDecimal(1111.2051161224));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getMiddleBand(), toBigDecimal(1322.2394020408));
        assertEquals(result[45].getUpperBand(), toBigDecimal(1388.3513721428));
        assertEquals(result[45].getLowerBand(), toBigDecimal(1256.1274319388));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getMiddleBand(), toBigDecimal(1424.1412081633));
        assertEquals(result[72].getUpperBand(), toBigDecimal(1495.3482685713));
        assertEquals(result[72].getLowerBand(), toBigDecimal(1352.9341477553));
    }

    @Test
    public void testHighLowBandsWithPeriodFourteenAndFourPercentageShift() {
        HLBRequest request = buildRequest();
        request.setPeriod(14);
        request.setShiftPercentage(4);
        HLBResult[] result = new HighLowBands(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getMiddleBand());
        assertNull(result[0].getUpperBand());
        assertNull(result[0].getLowerBand());
        assertNull(result[12].getMiddleBand());
        assertNull(result[12].getUpperBand());
        assertNull(result[12].getLowerBand());
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getMiddleBand(), toBigDecimal(1285.1794607143));
        assertEquals(result[13].getUpperBand(), toBigDecimal(1336.5866391427));
        assertEquals(result[13].getLowerBand(), toBigDecimal(1233.7722822859));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getMiddleBand(), toBigDecimal(1168.6062589286));
        assertEquals(result[32].getUpperBand(), toBigDecimal(1215.3505092858));
        assertEquals(result[32].getLowerBand(), toBigDecimal(1121.8620085714));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getMiddleBand(), toBigDecimal(1319.6860839286));
        assertEquals(result[45].getUpperBand(), toBigDecimal(1372.4735272858));
        assertEquals(result[45].getLowerBand(), toBigDecimal(1266.8986405714));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getMiddleBand(), toBigDecimal(1429.2874839286));
        assertEquals(result[72].getUpperBand(), toBigDecimal(1486.4589832858));
        assertEquals(result[72].getLowerBand(), toBigDecimal(1372.1159845714));
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

    @Override
    protected HLBRequest buildRequest() {
        return HLBRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .build();
    }

}
