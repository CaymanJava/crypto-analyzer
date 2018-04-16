package pro.crypto.indicators.env;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.ENVRequest;
import pro.crypto.model.result.ENVResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
        assertTrue(isNull(result[0].getBasis()));
        assertTrue(isNull(result[0].getUpperEnvelope()));
        assertTrue(isNull(result[0].getLowerEnvelope()));
        assertTrue(isNull(result[8].getBasis()));
        assertTrue(isNull(result[8].getUpperEnvelope()));
        assertTrue(isNull(result[8].getLowerEnvelope()));
        assertTrue(isNull(result[18].getBasis()));
        assertTrue(isNull(result[18].getUpperEnvelope()));
        assertTrue(isNull(result[18].getLowerEnvelope()));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getBasis(), new BigDecimal(1251.0690100000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[19].getUpperEnvelope(), new BigDecimal(1313.6224605000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[19].getLowerEnvelope(), new BigDecimal(1188.5155595000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getBasis(), new BigDecimal(1192.7020150000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getUpperEnvelope(), new BigDecimal(1252.3371157500).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getLowerEnvelope(), new BigDecimal(1133.0669142500).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getBasis(), new BigDecimal(1288.3630000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getUpperEnvelope(), new BigDecimal(1352.7811500000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getLowerEnvelope(), new BigDecimal(1223.9448500000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getBasis(), new BigDecimal(1427.1024900000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getUpperEnvelope(), new BigDecimal(1498.4576145000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getLowerEnvelope(), new BigDecimal(1355.7473655000).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void testMovingAverageEnvelopesWithSMAAndPeriodTwentyAndPercentageSeven() {
        ENVResult[] result = new MovingAverageEnvelopes(buildENVRequest(7)).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getBasis()));
        assertTrue(isNull(result[0].getUpperEnvelope()));
        assertTrue(isNull(result[0].getLowerEnvelope()));
        assertTrue(isNull(result[8].getBasis()));
        assertTrue(isNull(result[8].getUpperEnvelope()));
        assertTrue(isNull(result[8].getLowerEnvelope()));
        assertTrue(isNull(result[18].getBasis()));
        assertTrue(isNull(result[18].getUpperEnvelope()));
        assertTrue(isNull(result[18].getLowerEnvelope()));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getBasis(), new BigDecimal(1251.0690100000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[19].getUpperEnvelope(), new BigDecimal(1338.6438407000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[19].getLowerEnvelope(), new BigDecimal(1163.4941793000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getBasis(), new BigDecimal(1192.7020150000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getUpperEnvelope(), new BigDecimal(1276.1911560500).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getLowerEnvelope(), new BigDecimal(1109.2128739500).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getBasis(), new BigDecimal(1288.3630000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getUpperEnvelope(), new BigDecimal(1378.5484100000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getLowerEnvelope(), new BigDecimal(1198.1775900000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getBasis(), new BigDecimal(1427.1024900000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getUpperEnvelope(), new BigDecimal(1526.9996643000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getLowerEnvelope(), new BigDecimal(1327.2053157000).setScale(10, BigDecimal.ROUND_HALF_UP));
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