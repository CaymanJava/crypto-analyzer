package pro.crypto.indicator.mfi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class MarketFacilitationIndexTest extends IndicatorAbstractTest {

    @Test
    public void testMarketFacilitationIndex() {
        IndicatorResult[] expectedResult = loadExpectedResult("market_facilitation_index.json", MFIResult[].class);
        MFIResult[] actualResult = new MarketFacilitationIndex(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {MARKET_FACILITATION_INDEX}, size: {0}}");
        new MarketFacilitationIndex(MFIRequest.builder()
                .originalData(new Tick[0])
                .build()).getResult();
    }


    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {MARKET_FACILITATION_INDEX}}");
        new MarketFacilitationIndex(MFIRequest.builder()
                .originalData(null)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return MFIRequest.builder()
                .originalData(originalData)
                .build();
    }

}
