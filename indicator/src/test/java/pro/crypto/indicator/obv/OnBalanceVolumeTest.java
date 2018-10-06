package pro.crypto.indicator.obv;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class OnBalanceVolumeTest extends IndicatorAbstractTest {

    @Test
    public void testOnBalanceVolume() {
        IndicatorResult[] expectedResult = loadExpectedResult("on_balance_volume.json", OBVResult[].class);
        OBVResult[] actualResult = new OnBalanceVolume(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ON_BALANCE_VOLUME}, size: {0}}");
        new OnBalanceVolume(new OBVRequest(new Tick[0])).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ON_BALANCE_VOLUME}}");
        new OnBalanceVolume(new OBVRequest(null)).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return new OBVRequest(originalData);
    }

}
