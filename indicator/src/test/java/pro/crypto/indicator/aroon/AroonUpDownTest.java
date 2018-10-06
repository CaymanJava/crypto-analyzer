package pro.crypto.indicator.aroon;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class AroonUpDownTest extends IndicatorAbstractTest {

    @Test
    public void testAroonWithPeriodFourteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("aroon_up_down.json", AroonResult[].class);
        AroonResult[] actualResult = new AroonUpDown(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {AROON_UP_DOWN}, size: {0}}");
        new AroonUpDown(AroonRequest.builder()
                .originalData(new Tick[0])
                .period(14)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {AROON_UP_DOWN}}");
        new AroonUpDown(AroonRequest.builder()
                .originalData(null)
                .period(14)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {AROON_UP_DOWN}, period: {20}, size: {19}}");
        new AroonUpDown(AroonRequest.builder()
                .originalData(new Tick[19])
                .period(20)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {AROON_UP_DOWN}, period: {-14}");
        new AroonUpDown(AroonRequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return AroonRequest.builder()
                .originalData(originalData)
                .period(14)
                .build();
    }

}