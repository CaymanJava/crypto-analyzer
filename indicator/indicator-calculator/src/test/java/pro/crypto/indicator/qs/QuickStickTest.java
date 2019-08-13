package pro.crypto.indicator.qs;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;

public class QuickStickTest extends IndicatorAbstractTest {

    @Test
    public void testQuickStick() {
        IndicatorResult[] expectedResult = loadExpectedResult("quick_stick.json", QSResult[].class);
        QSResult[] actualResult = new QuickStick(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {QUICK_STICK}, size: {0}}");
        new QuickStick(QSRequest.builder()
                .originalData(new Tick[0])
                .period(14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {QUICK_STICK}}");
        new QuickStick(QSRequest.builder()
                .originalData(null)
                .period(14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodsMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {QUICK_STICK}, period: {28}, size: {27}}");
        new QuickStick(QSRequest.builder()
                .originalData(new Tick[27])
                .period(28)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void periodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {QUICK_STICK}, period: {-14}");
        new QuickStick(QSRequest.builder()
                .originalData(new Tick[100])
                .period(-14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build()).getResult();
    }

    @Test
    public void unsupportedIndicatorTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {QUICK_STICK}}, movingAverageType: {AVERAGE_TRUE_RANGE}");
        new QuickStick(QSRequest.builder()
                .originalData(new Tick[100])
                .period(14)
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return QSRequest.builder()
                .originalData(originalData)
                .period(14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

}
