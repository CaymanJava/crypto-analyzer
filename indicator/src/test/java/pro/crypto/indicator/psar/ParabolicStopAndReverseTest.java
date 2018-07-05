package pro.crypto.indicator.psar;

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
        assertNull(result[0].getIndicatorValue());
        assertEquals(result[1].getTime(), of(2018, 2, 26, 0, 0));
        assertEquals(result[1].getIndicatorValue(), toBigDecimal(1302.67));
        assertEquals(result[9].getTime(), of(2018, 3, 6, 0, 0));
        assertEquals(result[9].getIndicatorValue(), toBigDecimal(1345.7364));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(1239.62));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(1140.99897824));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(1313.72));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1467.6677384358));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {PARABOLIC_STOP_AND_REVERSE}, size: {0}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(new Tick[0])
                .minAccelerationFactor(toBigDecimal(0.02))
                .maxAccelerationFactor(toBigDecimal(0.2))
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {PARABOLIC_STOP_AND_REVERSE}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(null)
                .minAccelerationFactor(toBigDecimal(0.02))
                .maxAccelerationFactor(toBigDecimal(0.2))
                .build()).getResult();
    }

    @Test
    public void minAccelerationFactorMoreThanMaxAccelerationFactorTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Min Acceleration Factor should be less than Max Acceleration Factor " +
                "{indicator: {PARABOLIC_STOP_AND_REVERSE}, minAccelerationFactor: {0.300}, maxAccelerationFactor: {0.200}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(new Tick[100])
                .minAccelerationFactor(toBigDecimal(0.3))
                .maxAccelerationFactor(toBigDecimal(0.2))
                .build()).getResult();
    }

    @Test
    public void minAccelerationFactorLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Min Acceleration Factor should be less more than zero " +
                "{indicator: {PARABOLIC_STOP_AND_REVERSE}, minAccelerationFactor: {-0.020}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(new Tick[100])
                .minAccelerationFactor(toBigDecimal(-0.02))
                .maxAccelerationFactor(toBigDecimal(0.2))
                .build()).getResult();
    }

    @Test
    public void maxAccelerationFactorLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Max Acceleration Factor should be less more than zero " +
                "{indicator: {PARABOLIC_STOP_AND_REVERSE}, minAccelerationFactor: {-0.200}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(new Tick[100])
                .minAccelerationFactor(toBigDecimal(0.02))
                .maxAccelerationFactor(toBigDecimal(-0.2))
                .build()).getResult();
    }

    private PSARRequest buildSARRequest() {
        return PSARRequest.builder()
                .originalData(originalData)
                .minAccelerationFactor(toBigDecimal(0.02))
                .maxAccelerationFactor(toBigDecimal(0.2))
                .build();
    }

}