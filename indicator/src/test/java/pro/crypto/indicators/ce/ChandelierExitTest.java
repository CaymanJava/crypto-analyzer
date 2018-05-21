package pro.crypto.indicators.ce;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.CERequest;
import pro.crypto.model.result.CEResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class ChandelierExitTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testChandelierExitWithDefaultParameters() {
        CEResult[] result = new ChandelierExit(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertNull(result[0].getLongChandelierExit());
        assertNull(result[0].getShortChandelierExit());
        assertEquals(result[8].getTime(), of(2018, 3, 5, 0, 0));
        assertNull(result[8].getLongChandelierExit());
        assertNull(result[8].getShortChandelierExit());
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertNull(result[20].getLongChandelierExit());
        assertNull(result[20].getShortChandelierExit());
        assertEquals(result[21].getTime(), of(2018, 3, 18, 0, 0));
        assertEquals(result[21].getLongChandelierExit(), toBigDecimal(1247.2594909092));
        assertEquals(result[21].getShortChandelierExit(), toBigDecimal(1260.0804090908));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getLongChandelierExit(), toBigDecimal(1174.0467906326));
        assertEquals(result[32].getShortChandelierExit(), toBigDecimal(1217.6532093674));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getLongChandelierExit(), toBigDecimal(1310.1626664502));
        assertEquals(result[45].getShortChandelierExit(), toBigDecimal(1218.3573335498));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getLongChandelierExit(), toBigDecimal(1417.0923441340));
        assertEquals(result[72].getShortChandelierExit(), toBigDecimal(1438.4775558660));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CHANDELIER_EXIT}, size: {0}}");
        new ChandelierExit(CERequest.builder()
                .originalData(new Tick[0])
                .period(22)
                .factor(3)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CHANDELIER_EXIT}}");
        new ChandelierExit(CERequest.builder()
                .originalData(null)
                .period(22)
                .factor(3)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CHANDELIER_EXIT}, period: {22}, size: {21}}");
        new ChandelierExit(CERequest.builder()
                .originalData(new Tick[21])
                .period(22)
                .factor(3)
                .build()).getResult();
    }

    @Test
    public void factorLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Chandelier exit factor should be more than 0 {indicator: {CHANDELIER_EXIT}, shift: {-3.00}}");
        new ChandelierExit(CERequest.builder()
                .originalData(new Tick[100])
                .period(22)
                .factor(-3)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CHANDELIER_EXIT}, period: {-22}}");
        new ChandelierExit(CERequest.builder()
                .originalData(new Tick[100])
                .period(-22)
                .factor(3)
                .build()).getResult();
    }

    private CERequest buildRequest() {
        return CERequest.builder()
                .originalData(originalData)
                .period(22)
                .factor(3)
                .build();
    }

}