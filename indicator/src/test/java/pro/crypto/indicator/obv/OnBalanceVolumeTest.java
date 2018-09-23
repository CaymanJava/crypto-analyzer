package pro.crypto.indicator.obv;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class OnBalanceVolumeTest extends IndicatorAbstractTest {

    @Test
    public void testOnBalanceVolume() {
        OBVResult[] result = new OnBalanceVolume(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getIndicatorValue(), toBigDecimal(15.5471));
        assertEquals(result[5].getTime(), of(2018, 3, 2, 0, 0));
        assertEquals(result[5].getIndicatorValue(), toBigDecimal(261.6899));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-555.4763));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(-775.6639));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(-55.216));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(486.9779));
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
