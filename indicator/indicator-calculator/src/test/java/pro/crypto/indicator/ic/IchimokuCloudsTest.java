package pro.crypto.indicator.ic;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IncreasedQuantityIndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class IchimokuCloudsTest extends IncreasedQuantityIndicatorAbstractTest {

    @Test
    public void testIchimokuCloudsWithDefaultParameters() {
        IndicatorResult[] expectedResult = loadExpectedResult("ichimoku_clouds.json", ICResult[].class);
        ICResult[] actualResult = new IchimokuClouds(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ICHIMOKU_CLOUDS}, size: {0}}");
        new IchimokuClouds(ICRequest.builder()
                .originalData(new Tick[0])
                .conversionLinePeriod(9)
                .baseLinePeriod(26)
                .leadingSpanPeriod(52)
                .displaced(26)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ICHIMOKU_CLOUDS}}");
        new IchimokuClouds(ICRequest.builder()
                .originalData(null)
                .conversionLinePeriod(9)
                .baseLinePeriod(26)
                .leadingSpanPeriod(52)
                .displaced(26)
                .build()).getResult();
    }

    @Test
    public void commonPeriodsLengthMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ICHIMOKU_CLOUDS}, period: {78}, size: {78}}");
        new IchimokuClouds(ICRequest.builder()
                .originalData(new Tick[78])
                .conversionLinePeriod(9)
                .baseLinePeriod(26)
                .leadingSpanPeriod(52)
                .displaced(26)
                .build()).getResult();
    }

    @Test
    public void conversionLinePeriodMoreThanBaseLinePeriod() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Conversion Line Period should be less than Base Line Period" +
                " {indicator: {ICHIMOKU_CLOUDS}, conversionLinePeriod: {27}, baseLinePeriod: {26}}");
        new IchimokuClouds(ICRequest.builder()
                .originalData(new Tick[200])
                .conversionLinePeriod(27)
                .baseLinePeriod(26)
                .leadingSpanPeriod(52)
                .displaced(26)
                .build()).getResult();
    }

    @Test
    public void baseLinePeriodMoreThanLeadingSpanPeriod() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Base Line Period should be less than Leading Span Period" +
                " {indicator: {ICHIMOKU_CLOUDS}, baseLinePeriod: {53}, leadingSpanPeriod: {52}}");
        new IchimokuClouds(ICRequest.builder()
                .originalData(new Tick[200])
                .conversionLinePeriod(9)
                .baseLinePeriod(53)
                .leadingSpanPeriod(52)
                .displaced(26)
                .build()).getResult();
    }

    @Test
    public void conversionLinePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ICHIMOKU_CLOUDS}, period: {-9}}");
        new IchimokuClouds(ICRequest.builder()
                .originalData(new Tick[200])
                .conversionLinePeriod(-9)
                .baseLinePeriod(26)
                .leadingSpanPeriod(52)
                .displaced(26)
                .build()).getResult();
    }

    @Test
    public void baseLinePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ICHIMOKU_CLOUDS}, period: {-26}}");
        new IchimokuClouds(ICRequest.builder()
                .originalData(new Tick[200])
                .conversionLinePeriod(9)
                .baseLinePeriod(-26)
                .leadingSpanPeriod(52)
                .displaced(26)
                .build()).getResult();
    }

    @Test
    public void leadingSpanPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ICHIMOKU_CLOUDS}, period: {-52}}");
        new IchimokuClouds(ICRequest.builder()
                .originalData(new Tick[200])
                .conversionLinePeriod(9)
                .baseLinePeriod(26)
                .leadingSpanPeriod(-52)
                .displaced(26)
                .build()).getResult();
    }

    @Test
    public void displacedValueLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Displaced value should be more than 0 {indicator: {ICHIMOKU_CLOUDS}, displaced: {-1}}");
        new IchimokuClouds(ICRequest.builder()
                .originalData(new Tick[200])
                .conversionLinePeriod(9)
                .baseLinePeriod(26)
                .leadingSpanPeriod(52)
                .displaced(-1)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return ICRequest.builder()
                .originalData(originalData)
                .conversionLinePeriod(9)
                .baseLinePeriod(26)
                .leadingSpanPeriod(52)
                .displaced(26)
                .build();
    }

}
