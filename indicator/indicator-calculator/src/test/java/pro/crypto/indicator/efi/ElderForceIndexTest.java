package pro.crypto.indicator.efi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class ElderForceIndexTest extends IndicatorAbstractTest {

    @Test
    public void testElderForceIndexWithPeriodThirteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("elder_force_index_1.json", EFIResult[].class);
        EFIResult[] actualResult = new ElderForceIndex(buildRequest(13)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testElderForceIndexWithPeriodTwo() {
        IndicatorResult[] expectedResult = loadExpectedResult("elder_force_index_2.json", EFIResult[].class);
        EFIResult[] actualResult = new ElderForceIndex(buildRequest(2)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ELDERS_FORCE_INDEX}, size: {0}}");
        new ElderForceIndex(EFIRequest.builder()
                .originalData(new Tick[0])
                .period(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ELDERS_FORCE_INDEX}}");
        new ElderForceIndex(EFIRequest.builder()
                .originalData(null)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ELDERS_FORCE_INDEX}, period: {21}, size: {20}}");
        new ElderForceIndex(EFIRequest.builder()
                .originalData(new Tick[20])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ELDERS_FORCE_INDEX}, period: {-14}");
        new ElderForceIndex(EFIRequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .build()).getResult();
    }

    @Override
    protected EFIRequest buildRequest() {
        return EFIRequest.builder()
                .originalData(originalData)
                .build();
    }

    private EFIRequest buildRequest(int period) {
        EFIRequest request = buildRequest();
        request.setPeriod(period);
        return request;
    }

}
