package pro.crypto.indicator.bb;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.KELTNER_CHANNEL;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class BollingerBandsTest extends IndicatorAbstractTest {

    @Test
    public void testBollingerBandsWithDefaultParameters() {
        IndicatorResult[] expectedResult = loadExpectedResult("bollinger_bands.json", BBResult[].class);
        BBResult[] actualResult = new BollingerBands(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {BOLLINGER_BANDS}, size: {0}}");
        new BollingerBands(BBRequest.builder()
                .originalData(new Tick[0])
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {BOLLINGER_BANDS}}");
        new BollingerBands(BBRequest.builder()
                .originalData(null)
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {BOLLINGER_BANDS}, period: {20}, size: {19}}");
        new BollingerBands(BBRequest.builder()
                .originalData(new Tick[19])
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {BOLLINGER_BANDS}, period: {-10}}");
        new BollingerBands(BBRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void coefficientLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Standard Deviation Coefficient should be more than 1 " +
                "{indicator: {BOLLINGER_BANDS}, standardDeviationCoefficient: {-2.00}}");
        new BollingerBands(BBRequest.builder()
                .originalData(new Tick[100])
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(-2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {BOLLINGER_BANDS}}");
        new BollingerBands(BBRequest.builder()
                .originalData(new Tick[100])
                .period(20)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {BOLLINGER_BANDS}}," +
                " movingAverageType: {KELTNER_CHANNEL}");
        new BollingerBands(BBRequest.builder()
                .originalData(new Tick[100])
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(KELTNER_CHANNEL)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return BBRequest.builder()
                .originalData(originalData)
                .period(20)
                .priceType(CLOSE)
                .standardDeviationCoefficient(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .build();
    }

}
