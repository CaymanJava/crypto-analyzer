package pro.crypto.indicator.lr;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class LinearRegressionTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testLinearRegressionWithAverageCalculation() {
        LRResult[] result = new LinearRegression(buildRequest(true)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[3].getIndicatorValue());
        assertEquals(result[4].getTime(), of(2018, 3, 1, 0, 0));
        assertEquals(result[4].getIndicatorValue(), toBigDecimal(1256.05705));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(1212.46296));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getIndicatorValue(), toBigDecimal(1082.422));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(1202.97098));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(1298.286));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1383.431));
    }

    @Test
    public void testLinearRegressionWithoutAverageCalculation() {
        LRResult[] result = new LinearRegression(buildRequest(false)).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[3].getIndicatorValue());
        assertEquals(result[4].getTime(), of(2018, 3, 1, 0, 0));
        assertEquals(result[4].getIndicatorValue(), toBigDecimal(1279.24703));
        assertEquals(result[19].getTime(), of(2018, 3, 16, 0, 0));
        assertEquals(result[19].getIndicatorValue(), toBigDecimal(1185.06504));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getIndicatorValue(), toBigDecimal(1145.58));
        assertEquals(result[32].getTime(), of(2018, 3, 29, 0, 0));
        assertEquals(result[32].getIndicatorValue(), toBigDecimal(1231.631));
        assertEquals(result[45].getTime(), of(2018, 4, 11, 0, 0));
        assertEquals(result[45].getIndicatorValue(), toBigDecimal(1336.468));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1362.703));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {LINEAR_REGRESSION}, size: {0}}");
        new LinearRegression(LRRequest.builder()
                .originalData(new Tick[0])
                .period(5)
                .priceType(CLOSE)
                .averageCalculation(true)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {LINEAR_REGRESSION}}");
        new LinearRegression(LRRequest.builder()
                .originalData(null)
                .period(5)
                .priceType(CLOSE)
                .averageCalculation(true)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {LINEAR_REGRESSION}, period: {20}, size: {19}}");
        new LinearRegression(LRRequest.builder()
                .originalData(new Tick[19])
                .period(20)
                .priceType(CLOSE)
                .averageCalculation(true)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {LINEAR_REGRESSION}, period: {-5}}");
        new LinearRegression(LRRequest.builder()
                .originalData(new Tick[100])
                .period(-5)
                .priceType(CLOSE)
                .averageCalculation(true)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {LINEAR_REGRESSION}}");
        new LinearRegression(LRRequest.builder()
                .originalData(new Tick[100])
                .period(5)
                .averageCalculation(true)
                .build()).getResult();
    }

    private IndicatorRequest buildRequest(boolean averageCalculation) {
        return LRRequest.builder()
                .originalData(originalData)
                .period(5)
                .priceType(CLOSE)
                .averageCalculation(averageCalculation)
                .build();
    }

}