package pro.crypto.indicators.fractal;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.request.FractalRequest;
import pro.crypto.model.result.FractalResult;
import pro.crypto.model.tick.Tick;

import java.util.stream.Stream;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;

public class FractalTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testFractalIndicator() {
        FractalResult[] result = new Fractal(new FractalRequest(originalData)).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(extractUpFractals(result).length == 8);
        assertTrue(extractDownFractals(result).length == 9);
        assertEquals(result[2].getTime(), of(2018, 2, 27, 0, 0));
        assertFalse(result[2].isUpFractal());
        assertTrue(result[2].isDownFractal());
        assertEquals(result[6].getTime(), of(2018, 3, 3, 0, 0));
        assertTrue(result[6].isUpFractal());
        assertFalse(result[6].isDownFractal());
        assertEquals(result[10].getTime(), of(2018, 3, 7, 0, 0));
        assertTrue(result[10].isUpFractal());
        assertFalse(result[10].isDownFractal());
        assertEquals(result[15].getTime(), of(2018, 3, 12, 0, 0));
        assertFalse(result[15].isUpFractal());
        assertTrue(result[15].isDownFractal());
        assertEquals(result[63].getTime(), of(2018, 4, 29, 0, 0));
        assertTrue(result[63].isUpFractal());
        assertFalse(result[63].isDownFractal());
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertFalse(result[68].isUpFractal());
        assertTrue(result[68].isDownFractal());
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

    private Boolean[] extractUpFractals(FractalResult[] result) {
        return Stream.of(result)
                .map(FractalResult::isUpFractal)
                .filter(fractal -> fractal)
                .toArray(Boolean[]::new);
    }

    private Boolean[] extractDownFractals(FractalResult[] result) {
        return Stream.of(result)
                .map(FractalResult::isDownFractal)
                .filter(fractal -> fractal)
                .toArray(Boolean[]::new);
    }

}