package pro.crypto.indicator.psar;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class ParabolicStopAndReverseTest extends IndicatorAbstractTest {

    @Test
    public void testParabolicSAR() {
        IndicatorResult[] expectedResult = loadExpectedResult("parabolic_stop_and_reverse.json", PSARResult[].class);
        PSARResult[] actualResult = new ParabolicStopAndReverse(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {PARABOLIC_STOP_AND_REVERSE}, size: {0}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(new Tick[0])
                .minAccelerationFactor(0.02)
                .maxAccelerationFactor(0.2)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {PARABOLIC_STOP_AND_REVERSE}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(null)
                .minAccelerationFactor(0.02)
                .maxAccelerationFactor(0.2)
                .build()).getResult();
    }

    @Test
    public void minAccelerationFactorMoreThanMaxAccelerationFactorTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Min Acceleration Factor should be less than Max Acceleration Factor " +
                "{indicator: {PARABOLIC_STOP_AND_REVERSE}, minAccelerationFactor: {0.300}, maxAccelerationFactor: {0.200}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(new Tick[100])
                .minAccelerationFactor(0.3)
                .maxAccelerationFactor(0.2)
                .build()).getResult();
    }

    @Test
    public void minAccelerationFactorLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Min Acceleration Factor should be less more than zero " +
                "{indicator: {PARABOLIC_STOP_AND_REVERSE}, minAccelerationFactor: {-0.020}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(new Tick[100])
                .minAccelerationFactor(-0.02)
                .maxAccelerationFactor(0.2)
                .build()).getResult();
    }

    @Test
    public void maxAccelerationFactorLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Max Acceleration Factor should be less more than zero " +
                "{indicator: {PARABOLIC_STOP_AND_REVERSE}, minAccelerationFactor: {-0.200}}");
        new ParabolicStopAndReverse(PSARRequest.builder()
                .originalData(new Tick[100])
                .minAccelerationFactor(0.02)
                .maxAccelerationFactor(-0.2)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return PSARRequest.builder()
                .originalData(originalData)
                .minAccelerationFactor(0.02)
                .maxAccelerationFactor(0.2)
                .build();
    }

}
