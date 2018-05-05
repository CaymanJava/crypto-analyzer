package pro.crypto.indicators.psar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.PSARRequest;
import pro.crypto.model.result.PSARResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParabolicStopAndReverseTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testParabolicSAR() {
        PSARResult[] result = new ParabolicStopAndReverse(buildSARRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getIndicatorValue()));
        assertEquals(result[1].getTime(), of(2018, 2, 26, 0, 0));
        assertEquals(result[1].getIndicatorValue(), new BigDecimal(1302.6700000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[9].getTime(), of(2018, 3, 6, 0, 0));
        assertEquals(result[9].getIndicatorValue(), new BigDecimal(1345.7364000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), new BigDecimal(1239.6200000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), new BigDecimal(1140.9989782400).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), new BigDecimal(1313.7200000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), new BigDecimal(1467.6677384358).setScale(10, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {PARABOLIC_STOP_AND_REVERSE}, size: {0}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(new Tick[0])
                .minAccelerationFactor(new BigDecimal(0.02))
                .maxAccelerationFactor(new BigDecimal(0.2))
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {PARABOLIC_STOP_AND_REVERSE}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(null)
                .minAccelerationFactor(new BigDecimal(0.02))
                .maxAccelerationFactor(new BigDecimal(0.2))
                .build()).getResult();
    }

    @Test
    public void minAccelerationFactorMoreThanMaxAccelerationFactorTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Min Acceleration Factor should be less than Max Acceleration Factor " +
                "{indicator: {PARABOLIC_STOP_AND_REVERSE}, minAccelerationFactor: {0.300}, maxAccelerationFactor: {0.200}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(new Tick[100])
                .minAccelerationFactor(new BigDecimal(0.3))
                .maxAccelerationFactor(new BigDecimal(0.2))
                .build()).getResult();
    }

    @Test
    public void minAccelerationFactorLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Min Acceleration Factor should be less more than zero " +
                "{indicator: {PARABOLIC_STOP_AND_REVERSE}, minAccelerationFactor: {-0.020}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(new Tick[100])
                .minAccelerationFactor(new BigDecimal(-0.02))
                .maxAccelerationFactor(new BigDecimal(0.2))
                .build()).getResult();
    }

    @Test
    public void maxAccelerationFactorLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Max Acceleration Factor should be less more than zero " +
                "{indicator: {PARABOLIC_STOP_AND_REVERSE}, minAccelerationFactor: {-0.200}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(new Tick[100])
                .minAccelerationFactor(new BigDecimal(0.02))
                .maxAccelerationFactor(new BigDecimal(-0.2))
                .build()).getResult();
    }

    private PSARRequest buildSARRequest() {
        return PSARRequest.builder()
                .originalData(originalData)
                .minAccelerationFactor(new BigDecimal(0.02))
                .maxAccelerationFactor(new BigDecimal(0.2))
                .build();
    }

}