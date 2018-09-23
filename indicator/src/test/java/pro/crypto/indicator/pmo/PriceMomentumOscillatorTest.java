package pro.crypto.indicator.pmo;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class PriceMomentumOscillatorTest extends IndicatorAbstractTest {

    @Test
    public void testPriceMomentumOscillatorWithShortCutPeriods() {
        PMORequest request = buildRequest();
        request.setSmoothingPeriod(25);
        request.setDoubleSmoothingPeriod(10);
        request.setSignalPeriod(5);
        PMOResult[] result = new PriceMomentumOscillator(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineValue());
        assertNull(result[8].getIndicatorValue());
        assertNull(result[33].getSignalLineValue());
        assertEquals(result[34].getTime(), of(2018, 3, 31, 0, 0));
        assertEquals(result[34].getIndicatorValue(), toBigDecimal(2.1939319177));
        assertNull(result[34].getSignalLineValue());
        assertEquals(result[38].getTime(), of(2018, 4, 4, 0, 0));
        assertEquals(result[38].getIndicatorValue(), toBigDecimal(4.2403814494));
        assertEquals(result[38].getSignalLineValue(), toBigDecimal(3.2952423935));
        assertEquals(result[52].getTime(), of(2018, 4, 18, 0, 0));
        assertEquals(result[52].getIndicatorValue(), toBigDecimal(4.2558640070));
        assertEquals(result[52].getSignalLineValue(), toBigDecimal(4.4164008825));
        assertEquals(result[65].getTime(), of(2018, 5, 1, 0, 0));
        assertEquals(result[65].getIndicatorValue(), toBigDecimal(4.2552976092));
        assertEquals(result[65].getSignalLineValue(), toBigDecimal(4.8712855063));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(0.2885799399));
        assertEquals(result[72].getSignalLineValue(), toBigDecimal(1.2539209896));
    }

    @Test
    public void testPriceMomentumOscillatorWithDefaultPeriods() {
        PMORequest request = buildRequest();
        request.setSmoothingPeriod(35);
        request.setDoubleSmoothingPeriod(20);
        request.setSignalPeriod(10);
        PMOResult[] result = new PriceMomentumOscillator(request).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineValue());
        assertNull(result[53].getIndicatorValue());
        assertNull(result[53].getSignalLineValue());
        assertEquals(result[54].getTime(), of(2018, 4, 20, 0, 0));
        assertEquals(result[54].getIndicatorValue(), toBigDecimal(2.4589712641));
        assertNull(result[54].getSignalLineValue());
        assertEquals(result[63].getTime(), of(2018, 4, 29, 0, 0));
        assertEquals(result[63].getIndicatorValue(), toBigDecimal(3.6459305667));
        assertEquals(result[63].getSignalLineValue(), toBigDecimal(3.0417257182));
        assertEquals(result[65].getTime(), of(2018, 5, 1, 0, 0));
        assertEquals(result[65].getIndicatorValue(), toBigDecimal(3.3919317422));
        assertEquals(result[65].getSignalLineValue(), toBigDecimal(3.1823840011));
        assertEquals(result[69].getTime(), of(2018, 5, 5, 0, 0));
        assertEquals(result[69].getIndicatorValue(), toBigDecimal(2.3472324895));
        assertEquals(result[69].getSignalLineValue(), toBigDecimal(2.9147822892));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(1.6791679092));
        assertEquals(result[72].getSignalLineValue(), toBigDecimal(2.4585087056));
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

}
