package pro.crypto.indicator.ha;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class HeikenAshiTest extends IndicatorAbstractTest {

    @Test
    public void testHeikenAshi() {
        IndicatorResult[] expectedResult = loadExpectedResult("heiken_ashi.json", HAResult[].class);
        HAResult[] actualResult = new HeikenAshi(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {HEIKEN_ASHI}, size: {0}}");
        new HeikenAshi(HARequest.builder()
                .originalData(new Tick[0])
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {HEIKEN_ASHI}}");
        new HeikenAshi(HARequest.builder()
                .originalData(null)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return new HARequest(originalData);
    }

}
