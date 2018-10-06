package pro.crypto.indicator.kst;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class KnowSureThingTest extends IndicatorAbstractTest {

    @Test
    public void testKnowSureThingWithRecommendedPeriods() {
        IndicatorResult[] expectedResult = loadExpectedResult("know_sure_thing.json", KSTResult[].class);
        KSTResult[] actualResult = new KnowSureThing(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
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

    @Override
    protected IndicatorRequest buildRequest() {
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
