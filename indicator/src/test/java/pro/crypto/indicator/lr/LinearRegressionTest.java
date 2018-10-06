package pro.crypto.indicator.lr;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class LinearRegressionTest extends IndicatorAbstractTest {

    @Test
    public void testLinearRegressionWithAverageCalculation() {
        IndicatorResult[] expectedResult = loadExpectedResult("linear_regression_1.json", LRResult[].class);
        LRResult[] actualResult = new LinearRegression(buildRequestWithAverageCalculation()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testLinearRegressionWithoutAverageCalculation() {
        IndicatorResult[] expectedResult = loadExpectedResult("linear_regression_2.json", LRResult[].class);
        LRResult[] actualResult = new LinearRegression(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
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

    @Override
    protected LRRequest buildRequest() {
        return LRRequest.builder()
                .originalData(originalData)
                .period(5)
                .priceType(CLOSE)
                .build();
    }

    private LRRequest buildRequestWithAverageCalculation() {
        LRRequest request = buildRequest();
        request.setAverageCalculation(true);
        return request;
    }

}
