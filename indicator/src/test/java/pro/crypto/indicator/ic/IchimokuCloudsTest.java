package pro.crypto.indicator.ic;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.tick.generator.IncreasedQuantityTickGenerator;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

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
        assertNull(result[0].getConversionLineValue());
        assertNull(result[0].getBaseLineValue());
        assertNull(result[0].getLeadingSpanAValue());
        assertNull(result[0].getLeadingSpanBValue());
        assertEquals(result[0].getLaggingSpanValue(), toBigDecimal(1114.11));
        assertEquals(result[7].getTime(), of(2018, 3, 4, 0, 0));
        assertNull(result[7].getConversionLineValue());
        assertNull(result[7].getBaseLineValue());
        assertNull(result[7].getLeadingSpanAValue());
        assertNull(result[7].getLeadingSpanBValue());
        assertEquals(result[7].getLaggingSpanValue(), toBigDecimal(1287.86));
        assertEquals(result[8].getTime(), of(2018, 3, 5, 0, 0));
        assertEquals(result[8].getConversionLineValue(), toBigDecimal(1299.135));
        assertNull(result[8].getBaseLineValue());
        assertNull(result[8].getLeadingSpanAValue());
        assertNull(result[8].getLeadingSpanBValue());
        assertEquals(result[8].getLaggingSpanValue(), toBigDecimal(1309.67));
        assertEquals(result[24].getTime(), of(2018, 3, 21, 0, 0));
        assertEquals(result[24].getConversionLineValue(), toBigDecimal(1176.49));
        assertNull(result[24].getBaseLineValue());
        assertNull(result[24].getLeadingSpanAValue());
        assertNull(result[24].getLeadingSpanBValue());
        assertEquals(result[24].getLaggingSpanValue(), toBigDecimal(1349.5601));
        assertEquals(result[25].getTime(), of(2018, 3, 22, 0, 0));
        assertEquals(result[25].getConversionLineValue(), toBigDecimal(1174.63));
        assertEquals(result[25].getBaseLineValue(), toBigDecimal(1228.455));
        assertNull(result[25].getLeadingSpanAValue());
        assertNull(result[25].getLeadingSpanBValue());
        assertEquals(result[25].getLaggingSpanValue(), toBigDecimal(1361.28));
        assertEquals(result[51].getTime(), of(2018, 4, 17, 0, 0));
        assertEquals(result[51].getConversionLineValue(), toBigDecimal(1366.875));
        assertEquals(result[51].getBaseLineValue(), toBigDecimal(1264.26));
        assertEquals(result[51].getLeadingSpanAValue(), toBigDecimal(1201.5425));
        assertNull(result[51].getLeadingSpanBValue());
        assertEquals(result[51].getLaggingSpanValue(), toBigDecimal(1287.86));
        assertEquals(result[77].getTime(), of(2018, 5, 13, 0, 0));
        assertEquals(result[77].getConversionLineValue(), toBigDecimal(1302.55495));
        assertEquals(result[77].getBaseLineValue(), toBigDecimal(1357.42995));
        assertEquals(result[77].getLeadingSpanAValue(), toBigDecimal(1315.5675));
        assertEquals(result[77].getLeadingSpanBValue(), toBigDecimal(1264.26));
        assertEquals(result[77].getLaggingSpanValue(), toBigDecimal(1481.96));
        assertEquals(result[91].getTime(), of(2018, 5, 27, 0, 0));
        assertEquals(result[91].getConversionLineValue(), toBigDecimal(1349.60995));
        assertEquals(result[91].getBaseLineValue(), toBigDecimal(1319.685));
        assertEquals(result[91].getLeadingSpanAValue(), toBigDecimal(1433.747425));
        assertEquals(result[91].getLeadingSpanBValue(), toBigDecimal(1314.96495));
        assertNull(result[91].getLaggingSpanValue());
        assertEquals(result[116].getTime(), of(2018, 6, 21, 0, 0));
        assertEquals(result[116].getConversionLineValue(), toBigDecimal(1418.625));
        assertEquals(result[116].getBaseLineValue(), toBigDecimal(1420.2549));
        assertEquals(result[116].getLeadingSpanAValue(), toBigDecimal(1334.647475));
        assertEquals(result[116].getLeadingSpanBValue(), toBigDecimal(1357.42995));
        assertNull(result[116].getLaggingSpanValue());
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