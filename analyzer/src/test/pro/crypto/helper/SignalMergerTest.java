package pro.crypto.helper;

import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pro.crypto.model.Signal;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameters;
import static pro.crypto.model.Signal.*;

@RunWith(Parameterized.class)
public class SignalMergerTest {

    private final SignalMergerTestHelper testHelper;

    public SignalMergerTest(SignalMergerTestHelper testHelper) {
        this.testHelper = testHelper;
    }

    @Parameters
    public static Collection<Object> data() {
        return asList(
                of(BUY, BUY, BUY),
                of(SELL, SELL, SELL),
                of(SELL, BUY, NEUTRAL),
                of(BUY, SELL, NEUTRAL),
                of(SELL, NEUTRAL, SELL),
                of(NEUTRAL, BUY, BUY),
                of(NEUTRAL, NEUTRAL, NEUTRAL),
                of(SELL, null, SELL),
                of(null, BUY, BUY),
                of(null, null, NEUTRAL),
                of(BUY, BUY, BUY, BUY),
                of(BUY, BUY, SELL, NEUTRAL),
                of(BUY, SELL, SELL, NEUTRAL),
                of(SELL, SELL, SELL, SELL),
                of(null, SELL, SELL, SELL),
                of(SELL, null, SELL, SELL),
                of(SELL, SELL, null, SELL),
                of(SELL, BUY, null, NEUTRAL),
                of(null, BUY, null, BUY),
                of(NEUTRAL, BUY, null, BUY),
                of(NEUTRAL, BUY, SELL, NEUTRAL),
                of(null, null, null, NEUTRAL),
                of(null, BUY, SELL, NEUTRAL)
        );
    }

    @Test
    public void signalMergerTest() {
        assertEquals(testHelper.getActualResult(), testHelper.getExpectedValue());
    }

    private static SignalMergerTestHelper of(Signal firstSignal, Signal secondSignal,
                                             Signal expectedValue) {
        return new SignalMergerTestHelper(firstSignal, secondSignal, expectedValue);
    }

    private static SignalMergerTestHelper of(Signal firstSignal, Signal secondSignal,
                                             Signal thirdSignal, Signal expectedValue) {
        return new SignalMergerTestHelper(firstSignal, secondSignal, thirdSignal, expectedValue);
    }

    @Data
    private static class SignalMergerTestHelper {

        private final Signal actualResult;
        private final Signal expectedValue;

        SignalMergerTestHelper(Signal firstSignal, Signal secondSignal,
                               Signal expectedValue) {
            this.actualResult = new SignalMerger().merge(firstSignal, secondSignal);
            this.expectedValue = expectedValue;
        }

        SignalMergerTestHelper(Signal firstSignal, Signal secondSignal,
                               Signal thirdValue, Signal expectedValue) {
            this.actualResult = new SignalMerger().merge(firstSignal, secondSignal, thirdValue);
            this.expectedValue = expectedValue;
        }

    }

}
