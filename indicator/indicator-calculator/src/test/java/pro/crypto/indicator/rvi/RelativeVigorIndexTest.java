package pro.crypto.indicator.rvi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class RelativeVigorIndexTest extends IndicatorAbstractTest {

    @Test
    public void testRelativeVigorIndexWithPeriodTen() {
        IndicatorResult[] expectedResult = loadExpectedResult("relative_vigor_index.json", RVIResult[].class);
        RVIResult[] actualResult = new RelativeVigorIndex(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {RELATIVE_VIGOR_INDEX}, size: {0}}");
        new RelativeVigorIndex(RVIRequest.builder()
                .originalData(new Tick[0])
                .period(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {RELATIVE_VIGOR_INDEX}}");
        new RelativeVigorIndex(RVIRequest.builder()
                .originalData(null)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {RELATIVE_VIGOR_INDEX}, period: {-10}}");
        new RelativeVigorIndex(RVIRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RELATIVE_VIGOR_INDEX}, period: {16}, size: {15}}");
        new RelativeVigorIndex(RVIRequest.builder()
                .originalData(new Tick[15])
                .period(10)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return RVIRequest.builder()
                .originalData(originalData)
                .period(10)
                .build();
    }

}
