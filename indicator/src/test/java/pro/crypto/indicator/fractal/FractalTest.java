package pro.crypto.indicator.fractal;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;

public class FractalTest extends IndicatorAbstractTest {

    @Test
    public void testFractalIndicator() {
        IndicatorResult[] expectedResult = loadExpectedResult("fractal.json", FractalResult[].class);
        FractalResult[] actualResult = new Fractal(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {FRACTAL}, size: {0}}");
        new Fractal(new FractalRequest(new Tick[0])).getResult();
    }

    @Test
    public void originalDataSizeLessThanFiveTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("In Fractal indicator incoming tick data size should be >= 5 {indicator: {FRACTAL}, size: {4}}");
        new Fractal(new FractalRequest(new Tick[4])).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {FRACTAL}}");
        new Fractal(new FractalRequest(null)).getResult();
    }

    @Override
    protected FractalRequest buildRequest() {
        return new FractalRequest(originalData);
    }

}
