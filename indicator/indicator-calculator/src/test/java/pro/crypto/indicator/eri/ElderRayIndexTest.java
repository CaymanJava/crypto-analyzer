package pro.crypto.indicator.eri;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;

public class ElderRayIndexTest extends IndicatorAbstractTest {

    @Test
    public void testElderRayIndexWithPeriodThirteen() {
        IndicatorResult[] expectedResult = loadExpectedResult("elder_ray_index.json", ERIResult[].class);
        ERIResult[] actualResult = new ElderRayIndex(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
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
