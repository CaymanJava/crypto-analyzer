package pro.crypto.indicator.cci;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class CommodityChannelIndexTest extends IndicatorAbstractTest {

    @Test
    public void testCCIWithTwentyDaysPeriod() {
        IndicatorResult[] expectedResult = loadExpectedResult("commodity_channel_index.json", CCIResult[].class);
        CCIResult[] actualResult = new CommodityChannelIndex(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {COMMODITY_CHANNEL_INDEX}, size: {0}}");
        new CommodityChannelIndex(CCIRequest.builder()
                .originalData(new Tick[0])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {COMMODITY_CHANNEL_INDEX}}");
        new CommodityChannelIndex(CCIRequest.builder()
                .originalData(null)
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {COMMODITY_CHANNEL_INDEX}, period: {20}, size: {19}}");
        new CommodityChannelIndex(CCIRequest.builder()
                .originalData(new Tick[19])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {COMMODITY_CHANNEL_INDEX}, period: {-20}}");
        new CommodityChannelIndex(CCIRequest.builder()
                .originalData(new Tick[19])
                .period(-20)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return CCIRequest.builder()
                .originalData(originalData)
                .period(20)
                .build();
    }

}
