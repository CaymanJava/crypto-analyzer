package pro.crypto.indicator.ppo;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class PercentagePriceOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testPercentagePriceOscillatorWithRecommendedPeriods() {
        IndicatorResult[] expectedResult = loadExpectedResult("percentage_price_oscillator.json", PPOResult[].class);
        PPOResult[] actualResult = new PercentagePriceOscillator(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {PERCENTAGE_PRICE_OSCILLATOR}, size: {0}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[0])
                .fastPeriod(12)
                .slowPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {PERCENTAGE_PRICE_OSCILLATOR}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(null)
                .fastPeriod(12)
                .slowPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {PERCENTAGE_PRICE_OSCILLATOR}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(12)
                .slowPeriod(26)
                .signalPeriod(9)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void fastPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PERCENTAGE_PRICE_OSCILLATOR}, period: {-12}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(-12)
                .slowPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void slowPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PERCENTAGE_PRICE_OSCILLATOR}, period: {-26}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(12)
                .slowPeriod(-26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void signalPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PERCENTAGE_PRICE_OSCILLATOR}, period: {-9}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(12)
                .slowPeriod(26)
                .signalPeriod(-9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {PERCENTAGE_PRICE_OSCILLATOR}}, movingAverageType: {AVERAGE_TRUE_RANGE}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[100])
                .fastPeriod(12)
                .slowPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .build()).getResult();
    }

    @Test
    public void notEnoughDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {PERCENTAGE_PRICE_OSCILLATOR}, period: {35}, size: {34}}");
        new PercentagePriceOscillator(PPORequest.builder()
                .originalData(new Tick[34])
                .fastPeriod(12)
                .slowPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return PPORequest.builder()
                .originalData(originalData)
                .fastPeriod(12)
                .slowPeriod(26)
                .signalPeriod(9)
                .priceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

}
