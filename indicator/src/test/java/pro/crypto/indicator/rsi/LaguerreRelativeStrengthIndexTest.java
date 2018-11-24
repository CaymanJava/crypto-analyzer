package pro.crypto.indicator.rsi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class LaguerreRelativeStrengthIndexTest extends IndicatorAbstractTest {

    @Test
    public void testLaguerreRelativeStrengthIndex() {
        IndicatorResult[] expectedResult = loadExpectedResult("laguerre_relative_strength_index.json", RSIResult[].class);
        RSIResult[] actualResult = new LaguerreRelativeStrengthIndex(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {LAGUERRE_RELATIVE_STRENGTH_INDEX}, size: {0}}");
        new LaguerreRelativeStrengthIndex(LRSIRequest.builder()
                .originalData(new Tick[0])
                .gamma(0.5)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {LAGUERRE_RELATIVE_STRENGTH_INDEX}}");
        new LaguerreRelativeStrengthIndex(LRSIRequest.builder()
                .originalData(null)
                .gamma(0.5)
                .build()).getResult();
    }

    @Test
    public void gammaLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Parameter gamma should be between 0 and 1 {indicator: {LAGUERRE_RELATIVE_STRENGTH_INDEX}}");
        new LaguerreRelativeStrengthIndex(LRSIRequest.builder()
                .originalData(new Tick[100])
                .gamma(-0.5)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return LRSIRequest.builder()
                .originalData(originalData)
                .gamma(0.5)
                .build();
    }

}
