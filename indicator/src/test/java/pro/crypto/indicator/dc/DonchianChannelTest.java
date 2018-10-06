package pro.crypto.indicator.dc;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class DonchianChannelTest extends IndicatorAbstractTest {

    @Test
    public void testDonchainChannelWithDefaultParameters() {
        IndicatorResult[] expectedResult = loadExpectedResult("donchain_channel.json", DCResult[].class);
        DCResult[] actualResult = new DonchianChannel(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {DONCHIAN_CHANNEL}, size: {0}}");
        new DonchianChannel(DCRequest.builder()
                .originalData(new Tick[0])
                .highPeriod(20)
                .lowPeriod(20)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {DONCHIAN_CHANNEL}}");
        new DonchianChannel(DCRequest.builder()
                .originalData(null)
                .highPeriod(20)
                .lowPeriod(20)
                .build()).getResult();
    }

    @Test
    public void highPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {DONCHIAN_CHANNEL}, period: {20}, size: {19}}");
        new DonchianChannel(DCRequest.builder()
                .originalData(new Tick[19])
                .highPeriod(20)
                .lowPeriod(10)
                .build()).getResult();
    }

    @Test
    public void lowPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {DONCHIAN_CHANNEL}, period: {20}, size: {19}}");
        new DonchianChannel(DCRequest.builder()
                .originalData(new Tick[19])
                .highPeriod(10)
                .lowPeriod(20)
                .build()).getResult();
    }

    @Test
    public void highPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {DONCHIAN_CHANNEL}, period: {-20}}");
        new DonchianChannel(DCRequest.builder()
                .originalData(new Tick[100])
                .highPeriod(-20)
                .lowPeriod(20)
                .build()).getResult();
    }

    @Test
    public void lowPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {DONCHIAN_CHANNEL}, period: {-20}}");
        new DonchianChannel(DCRequest.builder()
                .originalData(new Tick[100])
                .highPeriod(20)
                .lowPeriod(-20)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return DCRequest.builder()
                .originalData(originalData)
                .highPeriod(20)
                .lowPeriod(20)
                .build();
    }

}
