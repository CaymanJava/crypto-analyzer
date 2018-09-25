package pro.crypto.indicator.eri;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.helper.MathHelper.toBigDecimal;

public class ElderRayIndexTest extends IndicatorAbstractTest {

    @Test
    public void testElderRayIndexWithPeriodThirteen() {
        ERIResult[] result = new ElderRayIndex(buildRequest()).getResult();
        assertTrue(result.length == originalData.length);
        assertNull(result[0].getMovingAverageValue());
        assertNull(result[0].getIndicatorValue());
        assertNull(result[0].getSignalLineValue());
        assertNull(result[0].getSmoothedLineValue());
        assertEquals(result[12].getTime(), of(2018, 3, 9, 0, 0));
        assertEquals(result[12].getMovingAverageValue(), toBigDecimal(1278.3130769231));
        assertEquals(result[12].getIndicatorValue(), toBigDecimal(-48.7631269231));
        assertNull(result[12].getSignalLineValue());
        assertNull(result[12].getSmoothedLineValue());
        assertEquals(result[13].getTime(), of(2018, 3, 10, 0, 0));
        assertEquals(result[13].getMovingAverageValue(), toBigDecimal(1270.1383516459));
        assertEquals(result[13].getIndicatorValue(), toBigDecimal(-45.5383516459));
        assertEquals(result[13].getSmoothedLineValue(), toBigDecimal(-47.1507392845));
        assertNull(result[13].getSignalLineValue());
        assertEquals(result[24].getTime(), of(2018, 3, 21, 0, 0));
        assertEquals(result[24].getMovingAverageValue(), toBigDecimal(1189.2829929561));
        assertEquals(result[24].getIndicatorValue(), toBigDecimal(-59.7079929561));
        assertEquals(result[24].getSignalLineValue(), toBigDecimal(-40.8745446567));
        assertEquals(result[24].getSmoothedLineValue(), toBigDecimal(-48.232189685));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getMovingAverageValue(), toBigDecimal(1406.8931772583));
        assertEquals(result[72].getIndicatorValue(), toBigDecimal(-31.7581272583));
        assertEquals(result[72].getSignalLineValue(), toBigDecimal(2.7156482559));
        assertEquals(result[72].getSmoothedLineValue(), toBigDecimal(-26.0631648335));
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ELDER_RAY_INDEX}, size: {0}}");
        new ElderRayIndex(ERIRequest.builder()
                .originalData(new Tick[0])
                .period(13)
                .signalLinePeriod(13)
                .smoothLinePeriod(2)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ELDER_RAY_INDEX}}");
        new ElderRayIndex(ERIRequest.builder()
                .originalData(null)
                .period(13)
                .signalLinePeriod(13)
                .smoothLinePeriod(2)
                .build()).getResult();
    }

    @Test
    public void periodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ELDER_RAY_INDEX}, period: {26}, size: {25}}");
        new ElderRayIndex(ERIRequest.builder()
                .originalData(new Tick[25])
                .period(13)
                .signalLinePeriod(13)
                .smoothLinePeriod(2)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ELDER_RAY_INDEX}, period: {-13}");
        new ElderRayIndex(ERIRequest.builder()
                .originalData(new Tick[100])
                .period(-13)
                .signalLinePeriod(13)
                .smoothLinePeriod(2)
                .build()).getResult();
    }

    @Test
    public void signalLinePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ELDER_RAY_INDEX}, period: {-13}");
        new ElderRayIndex(ERIRequest.builder()
                .originalData(new Tick[100])
                .period(13)
                .signalLinePeriod(-13)
                .smoothLinePeriod(2)
                .build()).getResult();
    }

    @Test
    public void smoothedLinePeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ELDER_RAY_INDEX}, period: {-2}");
        new ElderRayIndex(ERIRequest.builder()
                .originalData(new Tick[100])
                .period(13)
                .signalLinePeriod(13)
                .smoothLinePeriod(-2)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return ERIRequest.builder()
                .originalData(originalData)
                .period(13)
                .signalLinePeriod(13)
                .smoothLinePeriod(2)
                .build();
    }

}
