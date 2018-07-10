package pro.crypto.indicator.rwi;

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

public class RandomWalkIndexTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testRandomWalkIndexWithPeriodFourteen() {
        RWIResult[] result = new RandomWalkIndex(buildRequest(14)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getHighValue());
        assertNull(result[0].getLowValue());
        assertNull(result[25].getHighValue());
        assertNull(result[25].getLowValue());
        assertEquals(result[26].getTime(), of(2018, 3, 23, 0, 0));
        assertEquals(result[26].getHighValue(), toBigDecimal(0.5394341732));
        assertEquals(result[26].getLowValue(), toBigDecimal(1.3226906514));
        assertEquals(result[34].getTime(), of(2018, 3, 31, 0, 0));
        assertEquals(result[34].getHighValue(), toBigDecimal(2.0410434279));
        assertEquals(result[34].getLowValue(), toBigDecimal(0.3794630675));
        assertEquals(result[41].getTime(), of(2018, 4, 7, 0, 0));
        assertEquals(result[41].getHighValue(), toBigDecimal(1.1215016535));
        assertEquals(result[41].getLowValue(), toBigDecimal(0.7091580678));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getHighValue(), toBigDecimal(1.7854040072));
        assertEquals(result[57].getLowValue(), toBigDecimal(-0.0629205804));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getHighValue(), toBigDecimal(0.2526913411));
        assertEquals(result[72].getLowValue(), toBigDecimal(1.3749889238));
    }

    @Test
    public void testRandomWalkIndexWithPeriodTen() {
        RWIResult[] result = new RandomWalkIndex(buildRequest(10)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getHighValue());
        assertNull(result[0].getLowValue());
        assertNull(result[17].getHighValue());
        assertNull(result[17].getLowValue());
        assertEquals(result[18].getTime(), of(2018, 3, 15, 0, 0));
        assertEquals(result[18].getHighValue(), toBigDecimal(1.0783547851));
        assertEquals(result[18].getLowValue(), toBigDecimal(1.0265120513));
        assertEquals(result[34].getTime(), of(2018, 3, 31, 0, 0));
        assertEquals(result[34].getHighValue(), toBigDecimal(1.9905616525));
        assertEquals(result[34].getLowValue(), toBigDecimal(0.3682471071));
        assertEquals(result[41].getTime(), of(2018, 4, 7, 0, 0));
        assertEquals(result[41].getHighValue(), toBigDecimal(1.0381015207));
        assertEquals(result[41].getLowValue(), toBigDecimal(0.7021658435));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getHighValue(), toBigDecimal(1.7851157996));
        assertEquals(result[57].getLowValue(), toBigDecimal(-0.0629038070));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getHighValue(), toBigDecimal(0.2524383844));
        assertEquals(result[72].getLowValue(), toBigDecimal(1.3786223641));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {RANDOM_WALK_INDEX}, size: {0}}");
        new RandomWalkIndex(RWIRequest.builder()
                .originalData(new Tick[0])
                .period(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {RANDOM_WALK_INDEX}}");
        new RandomWalkIndex(RWIRequest.builder()
                .originalData(null)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {RANDOM_WALK_INDEX}, period: {-14}}");
        new RandomWalkIndex(RWIRequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RANDOM_WALK_INDEX}, period: {28}, size: {27}}");
        new RandomWalkIndex(RWIRequest.builder()
                .originalData(new Tick[27])
                .period(14)
                .build()).getResult();
    }

    private IndicatorRequest buildRequest(int period) {
        return RWIRequest.builder()
                .originalData(originalData)
                .period(period)
                .build();
    }

}