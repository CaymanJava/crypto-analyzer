package pro.crypto.indicator.rma;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RainbowMovingAverageTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testRainbowMovingAverage() {
        RMAResult[] result = new RainbowMovingAverage(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertNull(result[0].getFirstMaValue());
        assertNull(result[0].getSecondMaValue());
        assertNull(result[0].getThirdMaValue());
        assertNull(result[0].getFourthMaValue());
        assertNull(result[0].getFifthMaValue());
        assertNull(result[0].getSixthMaValue());
        assertNull(result[0].getSeventhMaValue());
        assertNull(result[0].getEighthMaValue());
        assertNull(result[0].getNinthMaValue());
        assertNull(result[0].getTenthMaValue());
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getFirstMaValue(), toBigDecimal(1381.08));
        assertEquals(result[72].getSecondMaValue(), toBigDecimal(1389.575));
        assertEquals(result[72].getThirdMaValue(), toBigDecimal(1392.72875));
        assertEquals(result[72].getFourthMaValue(), toBigDecimal(1391.918125));
        assertEquals(result[72].getFifthMaValue(), toBigDecimal(1390.387496875));
        assertEquals(result[72].getSixthMaValue(), toBigDecimal(1390.269521875));
        assertEquals(result[72].getSeventhMaValue(), toBigDecimal(1392.1295929688));
        assertEquals(result[72].getEighthMaValue(), toBigDecimal(1395.6934546876));
        assertEquals(result[72].getNinthMaValue(), toBigDecimal(1400.5294675782));
        assertEquals(result[72].getTenthMaValue(), toBigDecimal(1406.3322214845));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {RAINBOW_MOVING_AVERAGE}, size: {0}}");
        new RainbowMovingAverage(RMARequest.builder()
                .originalData(new Tick[0])
                .period(2)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {RAINBOW_MOVING_AVERAGE}}");
        new RainbowMovingAverage(RMARequest.builder()
                .originalData(null)
                .period(2)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {RAINBOW_MOVING_AVERAGE}}");
        new RainbowMovingAverage(RMARequest.builder()
                .originalData(new Tick[100])
                .period(2)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {RAINBOW_MOVING_AVERAGE}, period: {-2}}");
        new RainbowMovingAverage(RMARequest.builder()
                .originalData(new Tick[100])
                .period(-2)
                .priceType(CLOSE)
                .build()).getResult();
    }

    @Test
    public void notEnoughDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RAINBOW_MOVING_AVERAGE}, period: {110}, size: {100}}");
        new RainbowMovingAverage(RMARequest.builder()
                .originalData(new Tick[100])
                .period(12)
                .priceType(CLOSE)
                .build()).getResult();
    }

    private RMARequest buildRequest() {
        return RMARequest.builder()
                .originalData(originalData)
                .period(2)
                .priceType(CLOSE)
                .build();
    }

}