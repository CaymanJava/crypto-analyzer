package pro.crypto.indicator.rvi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.*;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class RelativeVigorIndexTest extends IndicatorAbstractTest {

    @Test
    public void testRelativeVigorIndexWithPeriodTen() {
        RVIResult[] result = new RelativeVigorIndex(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineValue());
        assertNull(result[11].getIndicatorValue());
        assertNull(result[11].getSignalLineValue());
        assertEquals(result[12].getTime(), of(2018, 3, 9, 0, 0));
        assertEquals(result[12].getIndicatorValue(), toBigDecimal(-0.0889839203));
        assertNull(result[12].getSignalLineValue());
        assertEquals(result[15].getTime(), of(2018, 3, 12, 0, 0));
        assertEquals(result[15].getIndicatorValue(), toBigDecimal(-0.2435025382));
        assertEquals(result[15].getSignalLineValue(), toBigDecimal(-0.1477988867));
        assertEquals(result[33].getTime(), of(2018, 3, 30, 0, 0));
        assertEquals(result[33].getIndicatorValue(), toBigDecimal(0.1734617950));
        assertEquals(result[33].getSignalLineValue(), toBigDecimal(0.1003048945));
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getIndicatorValue(), toBigDecimal(0.0788192505));
        assertEquals(result[49].getSignalLineValue(), toBigDecimal(0.1485292446));
        assertEquals(result[58].getTime(), of(2018, 4, 24, 0, 0));
        assertEquals(result[58].getIndicatorValue(), toBigDecimal(0.1581438369));
        assertEquals(result[58].getSignalLineValue(), toBigDecimal(0.0632844493));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-0.2534211675));
        assertEquals(result[72].getSignalLineValue(), toBigDecimal(-0.2522008811));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {RELATIVE_VIGOR_INDEX}, size: {0}}");
        new RelativeVigorIndex(RVIRequest.builder()
                .originalData(new Tick[0])
                .period(10)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {RELATIVE_VIGOR_INDEX}}");
        new RelativeVigorIndex(RVIRequest.builder()
                .originalData(null)
                .period(10)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {RELATIVE_VIGOR_INDEX}, period: {-10}}");
        new RelativeVigorIndex(RVIRequest.builder()
                .originalData(new Tick[100])
                .period(-10)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {RELATIVE_VIGOR_INDEX}, period: {16}, size: {15}}");
        new RelativeVigorIndex(RVIRequest.builder()
                .originalData(new Tick[15])
                .period(10)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return RVIRequest.builder()
                .originalData(originalData)
                .period(10)
                .build();
    }

}
