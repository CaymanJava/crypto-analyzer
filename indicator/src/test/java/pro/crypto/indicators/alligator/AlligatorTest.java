package pro.crypto.indicators.alligator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.AlligatorRequest;
import pro.crypto.model.result.AlligatorResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.tick.TimeFrame.ONE_DAY;

public class AlligatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testAlligatorWithDefaultParameters() {
        AlligatorResult[] result = new Alligator(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getJawValue());
        assertNull(result[0].getTeethValue());
        assertNull(result[0].getLipsValue());
        assertNull(result[6].getJawValue());
        assertNull(result[6].getTeethValue());
        assertNull(result[6].getLipsValue());
        assertEquals(result[7].getTime(), of(2018, 3, 4, 0, 0));
        assertNull(result[7].getJawValue());
        assertNull(result[7].getTeethValue());
        assertEquals(result[7].getLipsValue(), toBigDecimal(1281.266));
        assertEquals(result[12].getTime(), of(2018, 3, 9, 0, 0));
        assertNull(result[12].getJawValue());
        assertEquals(result[12].getTeethValue(), toBigDecimal(1292.60375625));
        assertEquals(result[12].getLipsValue(), toBigDecimal(1290.052734576));
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertEquals(result[20].getJawValue(), toBigDecimal(1281.4792269231));
        assertEquals(result[20].getTeethValue(), toBigDecimal(1251.1338156589));
        assertEquals(result[20].getLipsValue(), toBigDecimal(1227.8517165009));
        assertEquals(result[56].getTime(), of(2018, 4, 22, 0, 0));
        assertEquals(result[56].getJawValue(), toBigDecimal(1311.3001312506));
        assertEquals(result[56].getTeethValue(), toBigDecimal(1341.5668294413));
        assertEquals(result[56].getLipsValue(), toBigDecimal(1370.4866862524));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getJawValue(), toBigDecimal(1405.0122727029));
        assertEquals(result[72].getTeethValue(), toBigDecimal(1432.1180753009));
        assertEquals(result[72].getLipsValue(), toBigDecimal(1421.3986480677));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ALLIGATOR}, size: {0}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[0])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ALLIGATOR}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(null)
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void jawPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ALLIGATOR}, period: {101}, size: {100}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(101)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void teethPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ALLIGATOR}, period: {101}, size: {100}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(101)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void lipsPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ALLIGATOR}, period: {101}, size: {100}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(101)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void jawPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ALLIGATOR}, period: {-13}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(-13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void teethPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ALLIGATOR}, period: {-8}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(-8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void lipsPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ALLIGATOR}, period: {-5}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(-5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void jawDisplacedLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Displaced value should be more than 0 {indicator: {ALLIGATOR}, displaced: {-8}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(-8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void teethDisplacedLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Displaced value should be more than 0 {indicator: {ALLIGATOR}, displaced: {-5}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(-5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void lipsDisplacedLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Displaced value should be more than 0 {indicator: {ALLIGATOR}, displaced: {-3}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(-3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    private AlligatorRequest buildRequest() {
        return AlligatorRequest.builder()
                .originalData(originalData)
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build();
    }

}