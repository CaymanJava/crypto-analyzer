package pro.crypto.indicator.dc;

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

public class DonchianChannelTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testDonchainChannelWithDefaultParameters() {
        DCResult[] result = new DonchianChannel(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getBasis());
        assertNull(result[0].getUpperEnvelope());
        assertNull(result[0].getLowerEnvelope());
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertEquals(result[20].getBasis(), toBigDecimal(1253.66995));
        assertEquals(result[20].getUpperEnvelope(), toBigDecimal(1347.27));
        assertEquals(result[20].getLowerEnvelope(), toBigDecimal(1160.0699));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getBasis(), toBigDecimal(1195.615));
        assertEquals(result[32].getUpperEnvelope(), toBigDecimal(1282.74));
        assertEquals(result[32].getLowerEnvelope(), toBigDecimal(1108.49));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getBasis(), toBigDecimal(1264.26));
        assertEquals(result[45].getUpperEnvelope(), toBigDecimal(1420.03));
        assertEquals(result[45].getLowerEnvelope(), toBigDecimal(1108.49));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getBasis(), toBigDecimal(1444.2549));
        assertEquals(result[72].getUpperEnvelope(), toBigDecimal(1521.4399));
        assertEquals(result[72].getLowerEnvelope(), toBigDecimal(1367.0699));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {DONCHIAN_CHANNEL}, size: {0}}");
        new DonchianChannel(DCRequest.builder()
                .originalData(new Tick[0])
                .highPeriod(20)
                .lowPeriod(20)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {DONCHIAN_CHANNEL}}");
        new DonchianChannel(DCRequest.builder()
                .originalData(null)
                .highPeriod(20)
                .lowPeriod(20)
                .build()).getResult();
    }

    @Test
    public void highPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {DONCHIAN_CHANNEL}, period: {20}, size: {19}}");
        new DonchianChannel(DCRequest.builder()
                .originalData(new Tick[19])
                .highPeriod(20)
                .lowPeriod(10)
                .build()).getResult();
    }

    @Test
    public void lowPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {DONCHIAN_CHANNEL}, period: {20}, size: {19}}");
        new DonchianChannel(DCRequest.builder()
                .originalData(new Tick[19])
                .highPeriod(10)
                .lowPeriod(20)
                .build()).getResult();
    }

    @Test
    public void highPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {DONCHIAN_CHANNEL}, period: {-20}}");
        new DonchianChannel(DCRequest.builder()
                .originalData(new Tick[100])
                .highPeriod(-20)
                .lowPeriod(20)
                .build()).getResult();
    }

    @Test
    public void lowPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {DONCHIAN_CHANNEL}, period: {-20}}");
        new DonchianChannel(DCRequest.builder()
                .originalData(new Tick[100])
                .highPeriod(20)
                .lowPeriod(-20)
                .build()).getResult();
    }

    private IndicatorRequest buildRequest() {
        return DCRequest.builder()
                .originalData(originalData)
                .highPeriod(20)
                .lowPeriod(20)
                .build();
    }

}