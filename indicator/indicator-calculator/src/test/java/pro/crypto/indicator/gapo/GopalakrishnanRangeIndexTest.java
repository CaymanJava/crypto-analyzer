package pro.crypto.indicator.gapo;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class GopalakrishnanRangeIndexTest extends IndicatorAbstractTest {

    @Test
    public void testGopalakrishnanRangeIndexWithPeriodFourteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("gopalakrishnan_range_index.json", GAPOResult[].class);
        GAPOResult[] actualResult = new GopalakrishnanRangeIndex(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {GOPALAKRISHNAN_RANGE_INDEX}, size: {0}}");
        new GopalakrishnanRangeIndex(GAPORequest.builder()
                .originalData(new Tick[0])
                .period(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {GOPALAKRISHNAN_RANGE_INDEX}}");
        new GopalakrishnanRangeIndex(GAPORequest.builder()
                .originalData(null)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {GOPALAKRISHNAN_RANGE_INDEX}, period: {14}, size: {13}}");
        new GopalakrishnanRangeIndex(GAPORequest.builder()
                .originalData(new Tick[13])
                .period(14)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {GOPALAKRISHNAN_RANGE_INDEX}, period: {-14}");
        new GopalakrishnanRangeIndex(GAPORequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return GAPORequest.builder()
                .originalData(originalData)
                .period(14)
                .build();
    }

}
