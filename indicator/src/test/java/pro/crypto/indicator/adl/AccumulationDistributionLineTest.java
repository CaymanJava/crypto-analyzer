package pro.crypto.indicator.adl;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class AccumulationDistributionLineTest extends IndicatorAbstractTest {

    @Test
    public void testAccumulationDistributionLine() {
        ADLResult[] result = new AccumulationDistributionLine(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getIndicatorValue(), toBigDecimal(-15.0661805778));
        assertEquals(result[5].getTime(), of(2018, 3, 2, 0, 0));
        assertEquals(result[5].getIndicatorValue(), toBigDecimal(132.4868391433));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(-253.1140191634));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(-440.5760543553));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(118.7231138961));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(347.8292776260));
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
