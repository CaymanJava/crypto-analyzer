package pro.crypto.indicator.vhf;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class VerticalHorizontalFilterTest extends IndicatorAbstractTest {

    @Test
    public void testVerticalHorizontalFilterWithDefaultPeriod() {
        IndicatorResult[] expectedResult = loadExpectedResult("vertical_horizontal_filter.json", VHFResult[].class);
        VHFResult[] actualResult = new VerticalHorizontalFilter(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {VERTICAL_HORIZONTAL_FILTER}, size: {0}}");
        new VerticalHorizontalFilter(VHFRequest.builder()
                .originalData(new Tick[0])
                .period(28)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {VERTICAL_HORIZONTAL_FILTER}}");
        new VerticalHorizontalFilter(VHFRequest.builder()
                .originalData(null)
                .period(28)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {VERTICAL_HORIZONTAL_FILTER}, period: {28}, size: {27}}");
        new VerticalHorizontalFilter(VHFRequest.builder()
                .originalData(new Tick[27])
                .period(28)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {VERTICAL_HORIZONTAL_FILTER}, period: {-28}}");
        new VerticalHorizontalFilter(VHFRequest.builder()
                .originalData(new Tick[100])
                .period(-28)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return VHFRequest.builder()
                .originalData(originalData)
                .period(28)
                .build();
    }

}
