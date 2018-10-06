package pro.crypto.indicator.pvt;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class PriceVolumeTrendTest extends IndicatorAbstractTest {

    @Test
    public void testPriceVolumeTrend() {
        IndicatorResult[] expectedResult = loadExpectedResult("price_volume_trend.json", PVTResult[].class);
        PVTResult[] actualResult = new PriceVolumeTrend(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {PRICE_VOLUME_TREND}}");
        new PriceVolumeTrend(PVTRequest.builder()
                .originalData(new Tick[100])
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return PVTRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .build();
    }

}
