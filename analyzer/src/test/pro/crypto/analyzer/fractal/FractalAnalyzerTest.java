package pro.crypto.analyzer.fractal;

import org.junit.Test;
import pro.crypto.analyzer.AnalyzerAbstractTest;
import pro.crypto.indicator.fractal.Fractal;
import pro.crypto.indicator.fractal.FractalRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.Signal.*;

public class FractalAnalyzerTest extends AnalyzerAbstractTest {

    @Test
    public void testFractalAnalyzer() {
        IndicatorResult[] indicatorResults = new Fractal(buildIndicatorRequest()).getResult();
        FractalAnalyzerResult[] result = new FractalAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[12].getTime(), of(2018, 3, 9, 0, 0));
        assertEquals(result[12].getSignal(), SELL);
        assertEquals(result[23].getTime(), of(2018, 3, 20, 0, 0));
        assertEquals(result[23].getSignal(), SELL);
        assertEquals(result[30].getTime(), of(2018, 3, 27, 0, 0));
        assertEquals(result[30].getSignal(), BUY);
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getSignal(), BUY);
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getSignal(), BUY);
        assertEquals(result[65].getTime(), of(2018, 5, 1, 0, 0));
        assertEquals(result[65].getSignal(), SELL);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), SELL);
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return new FractalRequest(originalData);
    }

}
