package pro.crypto.indicator.mi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class MassIndexTest extends IndicatorAbstractTest {

    @Test
    public void testMassIndexWithPeriodTwentyFive() {
        IndicatorResult[] expectedResult = loadExpectedResult("mass_index.json", MIResult[].class);
        MIResult[] actualResult = new MassIndex(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {MASS_INDEX}, size: {0}}");
        new MassIndex(MIRequest.builder()
                .originalData(new Tick[0])
                .period(25)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {MASS_INDEX}}");
        new MassIndex(MIRequest.builder()
                .originalData(null)
                .period(25)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {MASS_INDEX}, period: {43}, size: {42}}");
        new MassIndex(MIRequest.builder()
                .originalData(new Tick[42])
                .period(25)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {MASS_INDEX}, period: {-25}");
        new MassIndex(MIRequest.builder()
                .originalData(new Tick[100])
                .period(-25)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return MIRequest.builder()
                .originalData(originalData)
                .period(25)
                .build();
    }

}
