package pro.crypto.indicators.ic;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicators.tick.generator.IncreasedQuantityTickGenerator;
import pro.crypto.model.request.ICRequest;
import pro.crypto.model.result.ICResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IchimokuCloudsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new IncreasedQuantityTickGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testIchimokuCloudsWithDefaultParameters() {
        ICResult[] result = new IchimokuClouds(buildICRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertTrue(isNull(result[0].getConversionLineValue()));
        assertTrue(isNull(result[0].getBaseLineValue()));
        assertTrue(isNull(result[0].getLeadingSpanAValue()));
        assertTrue(isNull(result[0].getLeadingSpanBValue()));
        assertEquals(result[0].getLaggingSpanValue(), new BigDecimal(1114.1100000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[7].getTime(), of(2018, 3, 4, 0, 0));
        assertTrue(isNull(result[7].getConversionLineValue()));
        assertTrue(isNull(result[7].getBaseLineValue()));
        assertTrue(isNull(result[7].getLeadingSpanAValue()));
        assertTrue(isNull(result[7].getLeadingSpanBValue()));
        assertEquals(result[7].getLaggingSpanValue(), new BigDecimal(1287.8600000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[8].getTime(), of(2018, 3, 5, 0, 0));
        assertEquals(result[8].getConversionLineValue(), new BigDecimal(1299.1350000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[8].getBaseLineValue()));
        assertTrue(isNull(result[8].getLeadingSpanAValue()));
        assertTrue(isNull(result[8].getLeadingSpanBValue()));
        assertEquals(result[8].getLaggingSpanValue(), new BigDecimal(1309.6700000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[24].getTime(), of(2018, 3, 21, 0, 0));
        assertEquals(result[24].getConversionLineValue(), new BigDecimal(1176.4900000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[24].getBaseLineValue()));
        assertTrue(isNull(result[24].getLeadingSpanAValue()));
        assertTrue(isNull(result[24].getLeadingSpanBValue()));
        assertEquals(result[24].getLaggingSpanValue(), new BigDecimal(1349.5601000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[25].getTime(), of(2018, 3, 22, 0, 0));
        assertEquals(result[25].getConversionLineValue(), new BigDecimal(1174.6300000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[25].getBaseLineValue(), new BigDecimal(1228.4550000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[25].getLeadingSpanAValue()));
        assertTrue(isNull(result[25].getLeadingSpanBValue()));
        assertEquals(result[25].getLaggingSpanValue(), new BigDecimal(1361.2800000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[51].getTime(), of(2018, 4, 17, 0, 0));
        assertEquals(result[51].getConversionLineValue(), new BigDecimal(1366.8750000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[51].getBaseLineValue(), new BigDecimal(1264.2600000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[51].getLeadingSpanAValue(), new BigDecimal(1201.5425000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[51].getLeadingSpanBValue()));
        assertEquals(result[51].getLaggingSpanValue(), new BigDecimal(1287.8600000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[77].getTime(), of(2018, 5, 13, 0, 0));
        assertEquals(result[77].getConversionLineValue(), new BigDecimal(1302.5549500000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[77].getBaseLineValue(), new BigDecimal(1357.4299500000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[77].getLeadingSpanAValue(), new BigDecimal(1315.5675000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[77].getLeadingSpanBValue(), new BigDecimal(1264.2600000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[77].getLaggingSpanValue(), new BigDecimal(1481.9600000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[91].getTime(), of(2018, 5, 27, 0, 0));
        assertEquals(result[91].getConversionLineValue(), new BigDecimal(1349.6099500000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[91].getBaseLineValue(), new BigDecimal(1319.6850000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[91].getLeadingSpanAValue(), new BigDecimal(1433.7474250000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[91].getLeadingSpanBValue(), new BigDecimal(1314.9649500000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[91].getLaggingSpanValue()));
        assertEquals(result[116].getTime(), of(2018, 6, 21, 0, 0));
        assertEquals(result[116].getConversionLineValue(), new BigDecimal(1418.6250000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[116].getBaseLineValue(), new BigDecimal(1420.2549000000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[116].getLeadingSpanAValue(), new BigDecimal(1334.6474750000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertEquals(result[116].getLeadingSpanBValue(), new BigDecimal(1357.4299500000).setScale(10, BigDecimal.ROUND_HALF_UP));
        assertTrue(isNull(result[116].getLaggingSpanValue()));
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

    private ICRequest buildICRequest() {
        return ICRequest.builder()
                .originalData(originalData)
                .conversionLinePeriod(9)
                .baseLinePeriod(26)
                .leadingSpanPeriod(52)
                .displaced(26)
                .build();
    }

}