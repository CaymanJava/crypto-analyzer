package pro.crypto.indicator.alligator;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.TimeFrame.ONE_DAY;

public class AlligatorTest extends IndicatorAbstractTest {

    @Test
    public void testAlligatorWithDefaultParameters() {
        IndicatorResult[] expectedResult = loadExpectedResult("alligator.json", AlligatorResult[].class);
        AlligatorResult[] actualResult = new Alligator(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {ALLIGATOR}, size: {0}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[0])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {ALLIGATOR}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(null)
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void jawPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ALLIGATOR}, period: {101}, size: {100}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(101)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void teethPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ALLIGATOR}, period: {101}, size: {100}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(101)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void lipsPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {ALLIGATOR}, period: {101}, size: {100}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(101)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void jawPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ALLIGATOR}, period: {-13}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(-13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void teethPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ALLIGATOR}, period: {-8}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(-8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void lipsPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {ALLIGATOR}, period: {-5}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(-5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void jawDisplacedLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Displaced value should be more than 0 {indicator: {ALLIGATOR}, displaced: {-8}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(-8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void teethDisplacedLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Displaced value should be more than 0 {indicator: {ALLIGATOR}, displaced: {-5}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(-5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Test
    public void lipsDisplacedLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Displaced value should be more than 0 {indicator: {ALLIGATOR}, displaced: {-3}}");
        new Alligator(AlligatorRequest.builder()
                .originalData(new Tick[100])
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(-3)
                .timeFrame(ONE_DAY)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return AlligatorRequest.builder()
                .originalData(originalData)
                .jawPeriod(13)
                .jawOffset(8)
                .teethPeriod(8)
                .teethOffset(5)
                .lipsPeriod(5)
                .lipsOffset(3)
                .timeFrame(ONE_DAY)
                .build();
    }

}
