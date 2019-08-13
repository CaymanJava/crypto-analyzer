package pro.crypto.indicator.eft;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class EhlersFisherTransformTest extends IndicatorAbstractTest {

    @Test
    public void testEhlersFisherTransformWithDefaultPeriod() {
        IndicatorResult[] expectedResult = loadExpectedResult("ehlers_fisher_transform.json", EFTResult[].class);
        EFTResult[] actualResult = new EhlersFisherTransform(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {EHLERS_FISHER_TRANSFORM}, size: {0}}");
        new EhlersFisherTransform(EFTRequest.builder()
                .originalData(new Tick[0])
                .period(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {EHLERS_FISHER_TRANSFORM}}");
        new EhlersFisherTransform(EFTRequest.builder()
                .originalData(null)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {EHLERS_FISHER_TRANSFORM}, period: {10}, size: {9}}");
        new EhlersFisherTransform(EFTRequest.builder()
                .originalData(new Tick[9])
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {EHLERS_FISHER_TRANSFORM}, period: {-10}");
        new EhlersFisherTransform(EFTRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return EFTRequest.builder()
                .originalData(originalData)
                .period(10)
                .build();
    }

}
