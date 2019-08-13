package pro.crypto.indicator.adl;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class AccumulationDistributionLineTest extends IndicatorAbstractTest {

    @Test
    public void testAccumulationDistributionLine() {
        IndicatorResult[] expectedResult = loadExpectedResult("accumulation_distribution_line.json", ADLResult[].class);
        ADLResult[] actualResult = new AccumulationDistributionLine(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ACCUMULATION_DISTRIBUTION_LINE}, size: {0}}");
        new AccumulationDistributionLine(new ADLRequest(new Tick[0])).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ACCUMULATION_DISTRIBUTION_LINE}}");
        new AccumulationDistributionLine(new ADLRequest(null)).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return ADLRequest.builder()
                .originalData(originalData)
                .build();
    }

}
