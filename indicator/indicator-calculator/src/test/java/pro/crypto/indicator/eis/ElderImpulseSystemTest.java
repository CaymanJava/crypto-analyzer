package pro.crypto.indicator.eis;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.MOVING_AVERAGE_CONVERGENCE_DIVERGENCE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ElderImpulseSystemTest extends IndicatorAbstractTest {

    @Test
    public void testElderImpulseSystemWithDefaultPeriod() {
        IndicatorResult[] expectedResult = loadExpectedResult("elder_impulse_system.json", EISResult[].class);
        EISResult[] actualResult = new ElderImpulseSystem(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ELDER_IMPULSE_SYSTEM}, size: {0}}");
        new ElderImpulseSystem(EISRequest.builder()
                .originalData(new Tick[0])
                .maPeriod(13)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(12)
                .macdSlowPeriod(26)
                .macdSignalPeriod(9)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ELDER_IMPULSE_SYSTEM}}");
        new ElderImpulseSystem(EISRequest.builder()
                .maPeriod(13)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(12)
                .macdSlowPeriod(26)
                .macdSignalPeriod(9)
                .build()).getResult();
    }

    @Test
    public void maPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ELDER_IMPULSE_SYSTEM}, period: {13}, size: {12}}");
        new ElderImpulseSystem(EISRequest.builder()
                .originalData(new Tick[12])
                .maPeriod(13)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(12)
                .macdSlowPeriod(26)
                .macdSignalPeriod(9)
                .build()).getResult();
    }

    @Test
    public void macdPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ELDER_IMPULSE_SYSTEM}, period: {35}, size: {34}}");
        new ElderImpulseSystem(EISRequest.builder()
                .originalData(new Tick[34])
                .maPeriod(13)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(12)
                .macdSlowPeriod(26)
                .macdSignalPeriod(9)
                .build()).getResult();
    }

    @Test
    public void maEmptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {ELDER_IMPULSE_SYSTEM}}");
        new ElderImpulseSystem(EISRequest.builder()
                .originalData(new Tick[100])
                .maPeriod(13)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(12)
                .macdSlowPeriod(26)
                .macdSignalPeriod(9)
                .build()).getResult();
    }

    @Test
    public void maWrongOriginalIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {ELDER_IMPULSE_SYSTEM}}," +
                " movingAverageType: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}");
        new ElderImpulseSystem(EISRequest.builder()
                .originalData(new Tick[100])
                .maPeriod(13)
                .maType(MOVING_AVERAGE_CONVERGENCE_DIVERGENCE)
                .maPriceType(CLOSE)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(12)
                .macdSlowPeriod(26)
                .macdSignalPeriod(9)
                .build()).getResult();
    }

    @Test
    public void macdEmptyPriceTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming price type is null {indicator: {ELDER_IMPULSE_SYSTEM}}");
        new ElderImpulseSystem(EISRequest.builder()
                .originalData(new Tick[100])
                .maPeriod(13)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdFastPeriod(12)
                .macdSlowPeriod(26)
                .macdSignalPeriod(9)
                .build()).getResult();
    }

    @Test
    public void macdWrongOriginalIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {ELDER_IMPULSE_SYSTEM}}," +
                " movingAverageType: {MOVING_AVERAGE_CONVERGENCE_DIVERGENCE}");
        new ElderImpulseSystem(EISRequest.builder()
                .originalData(new Tick[100])
                .maPeriod(13)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .macdMaType(MOVING_AVERAGE_CONVERGENCE_DIVERGENCE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(12)
                .macdSlowPeriod(26)
                .macdSignalPeriod(9)
                .build()).getResult();
    }

    @Test
    public void maPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ELDER_IMPULSE_SYSTEM}, period: {-13}");
        new ElderImpulseSystem(EISRequest.builder()
                .originalData(new Tick[100])
                .maPeriod(-13)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(12)
                .macdSlowPeriod(26)
                .macdSignalPeriod(9)
                .build()).getResult();
    }

    @Test
    public void macdFastPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ELDER_IMPULSE_SYSTEM}, period: {-12}");
        new ElderImpulseSystem(EISRequest.builder()
                .originalData(new Tick[100])
                .maPeriod(13)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(-12)
                .macdSlowPeriod(26)
                .macdSignalPeriod(9)
                .build()).getResult();
    }

    @Test
    public void macdSlowPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ELDER_IMPULSE_SYSTEM}, period: {-26}");
        new ElderImpulseSystem(EISRequest.builder()
                .originalData(new Tick[100])
                .maPeriod(13)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(12)
                .macdSlowPeriod(-26)
                .macdSignalPeriod(9)
                .build()).getResult();
    }

    @Test
    public void macdSignalPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ELDER_IMPULSE_SYSTEM}, period: {-9}");
        new ElderImpulseSystem(EISRequest.builder()
                .originalData(new Tick[100])
                .maPeriod(13)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(12)
                .macdSlowPeriod(26)
                .macdSignalPeriod(-9)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return EISRequest.builder()
                .originalData(originalData)
                .maPeriod(13)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(12)
                .macdSlowPeriod(26)
                .macdSignalPeriod(9)
                .build();
    }

}
