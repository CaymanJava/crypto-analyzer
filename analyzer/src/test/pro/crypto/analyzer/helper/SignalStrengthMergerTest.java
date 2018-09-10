package pro.crypto.analyzer.helper;

import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pro.crypto.model.Signal;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.Strength;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameters;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.*;

@RunWith(Parameterized.class)
public class SignalStrengthMergerTest {

    private final SignalStrengthMergerTestHelper testHelper;

    public SignalStrengthMergerTest(SignalStrengthMergerTestHelper testHelper) {
        this.testHelper = testHelper;
    }

    @Parameters
    public static Collection<Object> data() {
        return asList(
                of(signal(BUY, WEAK), signal(BUY, STRONG), signal(BUY, STRONG)),
                of(signal(SELL, WEAK), signal(BUY, STRONG), signal(BUY, NORMAL)),
                of(null, signal(BUY, STRONG), signal(BUY, STRONG)),
                of(signal(NEUTRAL, UNDEFINED), signal(BUY, STRONG), signal(BUY, STRONG)),
                of(signal(NEUTRAL, UNDEFINED), signal(NEUTRAL, UNDEFINED), signal(NEUTRAL, UNDEFINED)),
                of(signal(SELL, WEAK), null, signal(SELL, WEAK)),
                of(signal(SELL, STRONG), signal(BUY, NORMAL), signal(SELL, WEAK)),
                of(null, null, signal(NEUTRAL, UNDEFINED)),
                of(signal(BUY, STRONG), signal(SELL, WEAK), signal(SELL, NORMAL), signal(NEUTRAL, UNDEFINED)),
                of(signal(BUY, STRONG), signal(SELL, NORMAL), signal(SELL, NORMAL), signal(SELL, WEAK)),
                of(signal(BUY, STRONG), signal(SELL, NORMAL), signal(SELL, STRONG), signal(SELL, NORMAL)),
                of(signal(BUY, WEAK), signal(SELL, WEAK), signal(SELL, STRONG), signal(SELL, STRONG)),
                of(signal(SELL, WEAK), signal(SELL, STRONG), signal(BUY, WEAK), signal(SELL, STRONG)),
                of(signal(SELL, WEAK), signal(SELL, WEAK), signal(BUY, WEAK), signal(SELL, WEAK)),
                of(signal(SELL, WEAK), signal(BUY, STRONG), signal(SELL, WEAK), signal(BUY, WEAK)),
                of(null, signal(BUY, STRONG), signal(SELL, WEAK), signal(BUY, NORMAL)),
                of(signal(BUY, NORMAL), null, signal(SELL, NORMAL), signal(NEUTRAL, UNDEFINED)),
                of(signal(BUY, STRONG), signal(SELL, NORMAL), null, signal(BUY, WEAK)),
                of(signal(BUY, STRONG), null, null, signal(BUY, STRONG)),
                of(null, signal(BUY, WEAK),null, signal(BUY, WEAK)),
                of(null, null, signal(SELL, NORMAL), signal(SELL, NORMAL)),
                of(null, null, null, signal(NEUTRAL, UNDEFINED))
        );
    }

    @Test
    public void signalStrengthMergerTest() {
        assertEquals(testHelper.getActualResult(), testHelper.getExpectedValue());
    }

    private static SignalStrengthMergerTestHelper of(SignalStrength firstSignal, SignalStrength secondSignal,
                                                     SignalStrength expectedValue) {
        return new SignalStrengthMergerTestHelper(firstSignal, secondSignal, expectedValue);
    }

    private static SignalStrengthMergerTestHelper of(SignalStrength firstSignal, SignalStrength secondSignal,
                                                     SignalStrength thirdSignal, SignalStrength expectedValue) {
        return new SignalStrengthMergerTestHelper(firstSignal, secondSignal, thirdSignal, expectedValue);
    }

    private static SignalStrength signal(Signal signal, Strength strength) {
        return new SignalStrength(signal, strength);
    }

    @Data
    private static class SignalStrengthMergerTestHelper {

        private final SignalStrength actualResult;
        private final SignalStrength expectedValue;

        SignalStrengthMergerTestHelper(SignalStrength firstSignal, SignalStrength secondSignal,
                                       SignalStrength expectedValue) {
            this.actualResult = new SignalStrengthMerger().merge(firstSignal, secondSignal);
            this.expectedValue = expectedValue;
        }

        SignalStrengthMergerTestHelper(SignalStrength firstSignal, SignalStrength secondSignal,
                                       SignalStrength thirdValue, SignalStrength expectedValue) {
            this.actualResult = new SignalStrengthMerger().merge(firstSignal, secondSignal, thirdValue);
            this.expectedValue = expectedValue;
        }

    }

}