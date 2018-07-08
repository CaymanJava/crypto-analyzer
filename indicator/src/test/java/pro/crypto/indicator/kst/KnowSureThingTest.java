package pro.crypto.indicator.kst;

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

public class KnowSureThingTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testKnowSureThingWithRecommendedPeriods() {
        KSTResult[] result = new KnowSureThing(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineValue());
        assertNull(result[43].getIndicatorValue());
        assertNull(result[43].getSignalLineValue());
        assertEquals(result[44].getTime(), of(2018, 4, 10, 0, 0));
        assertEquals(result[44].getIndicatorValue(), toBigDecimal(87.7155822778));
        assertNull(result[44].getSignalLineValue());
        assertEquals(result[52].getTime(), of(2018, 4, 18, 0, 0));
        assertEquals(result[52].getIndicatorValue(), toBigDecimal(116.0026406883));
        assertEquals(result[52].getSignalLineValue(), toBigDecimal(108.0579753731));
        assertEquals(result[57].getTime(), of(2018, 4, 23, 0, 0));
        assertEquals(result[57].getIndicatorValue(), toBigDecimal(113.5499541796));
        assertEquals(result[57].getSignalLineValue(), toBigDecimal(113.5655157421));
        assertEquals(result[59].getTime(), of(2018, 4, 25, 0, 0));
        assertEquals(result[59].getIndicatorValue(), toBigDecimal(121.2761401644));
        assertEquals(result[59].getSignalLineValue(), toBigDecimal(115.0203019548));
        assertEquals(result[68].getTime(), of(2018, 5, 4, 0, 0));
        assertEquals(result[68].getIndicatorValue(), toBigDecimal(98.8436335952));
        assertEquals(result[68].getSignalLineValue(), toBigDecimal(119.7698809121));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(54.8187314046));
        assertEquals(result[72].getSignalLineValue(), toBigDecimal(97.2596028365));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {KNOW_SURE_THING}, size: {0}}");
        new KnowSureThing(KSTRequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .lightestROCPeriod(10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(9)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {KNOW_SURE_THING}}");
        new KnowSureThing(KSTRequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .lightestROCPeriod(10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(9)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {KNOW_SURE_THING}}");
        new KnowSureThing(KSTRequest.builder()
                .originalData(new Tick[100])
                .lightestROCPeriod(10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(9)
                .build()).getResult();
    }

    @Test
    public void lightestROCPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KNOW_SURE_THING}, period: {-10}}");
        new KnowSureThing(KSTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .lightestROCPeriod(-10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(9)
                .build()).getResult();
    }

    @Test
    public void lightestSMAPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KNOW_SURE_THING}, period: {-10}}");
        new KnowSureThing(KSTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .lightestROCPeriod(10)
                .lightestSMAPeriod(-10)
                .lightROCPeriod(15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(9)
                .build()).getResult();
    }

    @Test
    public void lightROCPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KNOW_SURE_THING}, period: {-15}}");
        new KnowSureThing(KSTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .lightestROCPeriod(10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(-15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(9)
                .build()).getResult();
    }

    @Test
    public void lightSMAPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KNOW_SURE_THING}, period: {-10}}");
        new KnowSureThing(KSTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .lightestROCPeriod(10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(15)
                .lightSMAPeriod(-10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(9)
                .build()).getResult();
    }

    @Test
    public void heavyROCPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KNOW_SURE_THING}, period: {-20}}");
        new KnowSureThing(KSTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .lightestROCPeriod(10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(-20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(9)
                .build()).getResult();
    }

    @Test
    public void heavySMAPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KNOW_SURE_THING}, period: {-10}}");
        new KnowSureThing(KSTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .lightestROCPeriod(10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(-10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(9)
                .build()).getResult();
    }

    @Test
    public void heaviestROCPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KNOW_SURE_THING}, period: {-30}}");
        new KnowSureThing(KSTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .lightestROCPeriod(10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(-30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(9)
                .build()).getResult();
    }

    @Test
    public void heaviestSMAPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KNOW_SURE_THING}, period: {-15}}");
        new KnowSureThing(KSTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .lightestROCPeriod(10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(-15)
                .signalLinePeriod(9)
                .build()).getResult();
    }

    @Test
    public void signalPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {KNOW_SURE_THING}, period: {-9}}");
        new KnowSureThing(KSTRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .lightestROCPeriod(10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(-9)
                .build()).getResult();
    }

    @Test
    public void notEnoughDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {KNOW_SURE_THING}, period: {54}, size: {50}}");
        new KnowSureThing(KSTRequest.builder()
                .originalData(new Tick[50])
                .priceType(CLOSE)
                .lightestROCPeriod(10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(9)
                .build()).getResult();
    }

    private IndicatorRequest buildRequest() {
        return KSTRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .lightestROCPeriod(10)
                .lightestSMAPeriod(10)
                .lightROCPeriod(15)
                .lightSMAPeriod(10)
                .heavyROCPeriod(20)
                .heavySMAPeriod(10)
                .heaviestROCPeriod(30)
                .heaviestSMAPeriod(15)
                .signalLinePeriod(9)
                .build();
    }

}