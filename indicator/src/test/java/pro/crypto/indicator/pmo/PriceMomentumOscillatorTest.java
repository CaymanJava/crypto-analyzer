package pro.crypto.indicator.pmo;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class PriceMomentumOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testPriceMomentumOscillatorWithShortCutPeriods() {
        IndicatorResult[] expectedResult = loadExpectedResult("price_momentum_oscillator_1.json", PMOResult[].class);
        PMOResult[] actualResult = new PriceMomentumOscillator(buildRequest(25, 10, 5)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testPriceMomentumOscillatorWithDefaultPeriods() {
        IndicatorResult[] expectedResult = loadExpectedResult("price_momentum_oscillator_2.json", PMOResult[].class);
        PMOResult[] actualResult = new PriceMomentumOscillator(buildRequest(35, 20, 10)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {PRICE_MOMENTUM_OSCILLATOR}, size: {0}}");
        new PriceMomentumOscillator(PMORequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .smoothingPeriod(35)
                .doubleSmoothingPeriod(20)
                .signalPeriod(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {PRICE_MOMENTUM_OSCILLATOR}}");
        new PriceMomentumOscillator(PMORequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .smoothingPeriod(35)
                .doubleSmoothingPeriod(20)
                .signalPeriod(10)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {PRICE_MOMENTUM_OSCILLATOR}, period: {65}, size: {64}}");
        new PriceMomentumOscillator(PMORequest.builder()
                .originalData(new Tick[64])
                .priceType(CLOSE)
                .smoothingPeriod(35)
                .doubleSmoothingPeriod(20)
                .signalPeriod(10)
                .build()).getResult();
    }

    @Test
    public void smoothingPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PRICE_MOMENTUM_OSCILLATOR}, period: {-35}}");
        new PriceMomentumOscillator(PMORequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .smoothingPeriod(-35)
                .doubleSmoothingPeriod(20)
                .signalPeriod(10)
                .build()).getResult();
    }

    @Test
    public void doubleSmoothingPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PRICE_MOMENTUM_OSCILLATOR}, period: {-20}}");
        new PriceMomentumOscillator(PMORequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .smoothingPeriod(35)
                .doubleSmoothingPeriod(-20)
                .signalPeriod(10)
                .build()).getResult();
    }

    @Test
    public void signalPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {PRICE_MOMENTUM_OSCILLATOR}, period: {-10}}");
        new PriceMomentumOscillator(PMORequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .smoothingPeriod(35)
                .doubleSmoothingPeriod(20)
                .signalPeriod(-10)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {PRICE_MOMENTUM_OSCILLATOR}}");
        new PriceMomentumOscillator(PMORequest.builder()
                .originalData(new Tick[100])
                .smoothingPeriod(35)
                .doubleSmoothingPeriod(20)
                .signalPeriod(10)
                .build()).getResult();
    }

    @Override
    protected PMORequest buildRequest() {
        return PMORequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .build();
    }

    private PMORequest buildRequest(int smoothingPeriod, int doubleSmoothingPeriod, int signalPeriod) {
        PMORequest request = buildRequest();
        request.setSmoothingPeriod(smoothingPeriod);
        request.setDoubleSmoothingPeriod(doubleSmoothingPeriod);
        request.setSignalPeriod(signalPeriod);
        return request;
    }

}
