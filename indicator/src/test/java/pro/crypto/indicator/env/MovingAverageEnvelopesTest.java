package pro.crypto.indicator.env;

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
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;

public class MovingAverageEnvelopesTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testMovingAverageEnvelopesWithSMAAndPeriodTwentyAndPercentageFive() {
        ENVResult[] result = new MovingAverageEnvelopes(buildENVRequest(5)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getBasis());
        assertNull(result[0].getUpperEnvelope());
        assertNull(result[0].getLowerEnvelope());
        assertNull(result[8].getBasis());
        assertNull(result[8].getUpperEnvelope());
        assertNull(result[8].getLowerEnvelope());
        assertNull(result[18].getBasis());
        assertNull(result[18].getUpperEnvelope());
        assertNull(result[18].getLowerEnvelope());
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getBasis(), toBigDecimal(1251.06901));
        assertEquals(result[19].getUpperEnvelope(), toBigDecimal(1313.6224605));
        assertEquals(result[19].getLowerEnvelope(), toBigDecimal(1188.5155595));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getBasis(), toBigDecimal(1192.702015));
        assertEquals(result[32].getUpperEnvelope(), toBigDecimal(1252.33711575));
        assertEquals(result[32].getLowerEnvelope(), toBigDecimal(1133.06691425));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getBasis(), toBigDecimal(1288.363));
        assertEquals(result[45].getUpperEnvelope(), toBigDecimal(1352.78115));
        assertEquals(result[45].getLowerEnvelope(), toBigDecimal(1223.94485));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getBasis(), toBigDecimal(1427.10249));
        assertEquals(result[72].getUpperEnvelope(), toBigDecimal(1498.4576145));
        assertEquals(result[72].getLowerEnvelope(), toBigDecimal(1355.7473655));
    }

    @Test
    public void testMovingAverageEnvelopesWithSMAAndPeriodTwentyAndPercentageSeven() {
        ENVResult[] result = new MovingAverageEnvelopes(buildENVRequest(7)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getBasis());
        assertNull(result[0].getUpperEnvelope());
        assertNull(result[0].getLowerEnvelope());
        assertNull(result[8].getBasis());
        assertNull(result[8].getUpperEnvelope());
        assertNull(result[8].getLowerEnvelope());
        assertNull(result[18].getBasis());
        assertNull(result[18].getUpperEnvelope());
        assertNull(result[18].getLowerEnvelope());
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getBasis(), toBigDecimal(1251.06901));
        assertEquals(result[19].getUpperEnvelope(), toBigDecimal(1338.6438407));
        assertEquals(result[19].getLowerEnvelope(), toBigDecimal(1163.4941793));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getBasis(), toBigDecimal(1192.702015));
        assertEquals(result[32].getUpperEnvelope(), toBigDecimal(1276.19115605));
        assertEquals(result[32].getLowerEnvelope(), toBigDecimal(1109.21287395));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getBasis(), toBigDecimal(1288.363));
        assertEquals(result[45].getUpperEnvelope(), toBigDecimal(1378.54841));
        assertEquals(result[45].getLowerEnvelope(), toBigDecimal(1198.17759));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getBasis(), toBigDecimal(1427.10249));
        assertEquals(result[72].getUpperEnvelope(), toBigDecimal(1526.9996643));
        assertEquals(result[72].getLowerEnvelope(), toBigDecimal(1327.2053157));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {MOVING_AVERAGE_ENVELOPES}, size: {0}}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(new Tick[0])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .indentationPercentage(5)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {MOVING_AVERAGE_ENVELOPES}}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(null)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .indentationPercentage(5)
                .build()).getResult();
    }

    @Test
    public void movingAveragePeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {MOVING_AVERAGE_ENVELOPES}, period: {20}, size: {19}}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(new Tick[19])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .indentationPercentage(5)
                .build()).getResult();
    }

    @Test
    public void movingAveragePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {MOVING_AVERAGE_ENVELOPES}, period: {-20}}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(-20)
                .indentationPercentage(5)
                .build()).getResult();
    }

    @Test
    public void percentageMoreOrEqualsThanHundredTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Percentage should be in the range (0, 100) {indicator: {MOVING_AVERAGE_ENVELOPES}, indentationPercentage: {100}}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .indentationPercentage(100)
                .build()).getResult();
    }

    @Test
    public void percentageLessOrEqualsThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Percentage should be in the range (0, 100) {indicator: {MOVING_AVERAGE_ENVELOPES}, indentationPercentage: {0}}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .indentationPercentage(0)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {MOVING_AVERAGE_ENVELOPES}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new MovingAverageEnvelopes(ENVRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .movingAveragePeriod(20)
                .indentationPercentage(5)
                .build()).getResult();
    }

    private ENVRequest buildENVRequest(int percentage) {
        return ENVRequest.builder()
                .originalData(originalData)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .movingAveragePeriod(20)
                .indentationPercentage(percentage)
                .build();
    }

}