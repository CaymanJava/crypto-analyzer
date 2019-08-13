package pro.crypto.indicator.cc;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class CoppockCurveTest extends IndicatorAbstractTest {

    @Test
    public void testCoppockCurveWithDefaultParameters() {
        IndicatorResult[] expectedResult = loadExpectedResult("coppock_curve.json", CCResult[].class);
        CCResult[] actualResult = new CoppockCurve(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {COPPOCK_CURVE}, size: {0}}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[0])
                .priceType(CLOSE)
                .shortROCPeriod(11)
                .longROCPeriod(14)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {COPPOCK_CURVE}}");
        new CoppockCurve(CCRequest.builder()
                .originalData(null)
                .priceType(CLOSE)
                .shortROCPeriod(11)
                .longROCPeriod(14)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodSumMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {COPPOCK_CURVE}, period: {24}, size: {23}}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[23])
                .priceType(CLOSE)
                .shortROCPeriod(11)
                .longROCPeriod(14)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void longPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {COPPOCK_CURVE}, period: {-14}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .shortROCPeriod(11)
                .longROCPeriod(-14)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void shortPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {COPPOCK_CURVE}, period: {-11}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .shortROCPeriod(-11)
                .longROCPeriod(14)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {COPPOCK_CURVE}, period: {-10}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .shortROCPeriod(11)
                .longROCPeriod(14)
                .period(-10)
                .build()).getResult();
    }

    @Test
    public void longPeriodLessThanShortPeriodTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Short RoC Period should be less than Long Roc Period {indicator: {COPPOCK_CURVE}, conversionLinePeriod: {14}, baseLinePeriod: {11}}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[100])
                .priceType(CLOSE)
                .shortROCPeriod(14)
                .longROCPeriod(11)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void emptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {COPPOCK_CURVE}}");
        new CoppockCurve(CCRequest.builder()
                .originalData(new Tick[100])
                .shortROCPeriod(11)
                .longROCPeriod(14)
                .period(10)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return CCRequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .shortROCPeriod(11)
                .longROCPeriod(14)
                .period(10)
                .build();
    }

}
