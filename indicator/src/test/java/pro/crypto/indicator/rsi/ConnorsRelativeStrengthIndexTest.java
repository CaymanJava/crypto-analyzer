package pro.crypto.indicator.rsi;

import org.junit.Test;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;

public class ConnorsRelativeStrengthIndexTest extends IndicatorAbstractTest {

    @Test
    public void testConnorsRelativeStrengthIndexWithExponentialMovingAverageAndDefaultPeriods() {
        IndicatorResult[] expectedResult = loadExpectedResult("connors_relative_strength_index.json", RSIResult[].class);
        RSIResult[] actualResult = new ConnorsRelativeStrengthIndex(buildRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void emptyOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data size should be > 0 {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, size: {0}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[0])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(null)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void simpleRSIPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, period: {20}, size: {19}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[19])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .simpleRsiPeriod(20)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void streakRSIPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, period: {20}, size: {19}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[19])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(20)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void percentRankPeriodMoreThanTickDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be less than tick data size {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, period: {20}, size: {19}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[19])
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void simpleRSIPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, period: {-3}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .simpleRsiPeriod(-3)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void streakRSIPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, period: {-2}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(-2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Test
    public void percentRankPeriodLessThanZeroTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Period should be more than 0 {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}, period: {-20}}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(2)
                .percentRankPeriod(-20)
                .build()).getResult();
    }

    @Test
    public void wrongMovingAverageTypeTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming original indicator type is not a moving average {indicator: {CONNORS_RELATIVE_STRENGTH_INDEX}}," +
                " movingAverageType: {AVERAGE_TRUE_RANGE}");
        new ConnorsRelativeStrengthIndex(CRSIRequest.builder()
                .originalData(new Tick[100])
                .movingAverageType(AVERAGE_TRUE_RANGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build()).getResult();
    }

    @Override
    protected IndicatorRequest buildRequest() {
        return CRSIRequest.builder()
                .originalData(originalData)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .simpleRsiPeriod(3)
                .streakRsiPeriod(2)
                .percentRankPeriod(20)
                .build();
    }

}
