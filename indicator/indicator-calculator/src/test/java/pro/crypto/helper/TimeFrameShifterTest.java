package pro.crypto.helper;

import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pro.crypto.model.indicator.Shift;
import pro.crypto.model.indicator.ShiftType;
import pro.crypto.model.tick.TimeFrame;

import java.time.LocalDateTime;
import java.util.Collection;

import static java.time.LocalDateTime.of;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameters;
import static pro.crypto.model.indicator.ShiftType.LEFT;
import static pro.crypto.model.indicator.ShiftType.RIGHT;
import static pro.crypto.model.tick.TimeFrame.FIVE_MIN;
import static pro.crypto.model.tick.TimeFrame.ONE_HOUR;
import static pro.crypto.model.tick.TimeFrame.THIRTY_MIN;

@RunWith(Parameterized.class)
public class TimeFrameShifterTest {

    private final TestHelper testHelper;

    public TimeFrameShifterTest(TestHelper testHelper) {
        this.testHelper = testHelper;
    }

    @Parameters
    public static Collection<Object> data() {
        return asList(
                new TestHelper(RIGHT, 5, FIVE_MIN, of(2018, 1, 12, 0, 0), of(2018, 1, 12, 0, 25)),
                new TestHelper(RIGHT, 3, FIVE_MIN, of(2018, 1, 12, 23, 55), of(2018, 1, 13, 0, 10)),
                new TestHelper(LEFT, 7, FIVE_MIN, of(2018, 1, 12, 0, 10), of(2018, 1, 11, 23, 35)),
                new TestHelper(RIGHT, 3, THIRTY_MIN, of(2018, 1, 12, 23, 30), of(2018, 1, 13, 1, 0)),
                new TestHelper(LEFT, 5, THIRTY_MIN, of(2018, 1, 12, 0, 30), of(2018, 1, 11, 22, 0)),
                new TestHelper(RIGHT, 5, ONE_HOUR, of(2018, 1, 12, 0, 0), of(2018, 1, 12, 5, 0)),
                new TestHelper(LEFT, 3, ONE_HOUR, of(2018, 1, 12, 0, 0), of(2018, 1, 11, 21, 0))
        );
    }

    @Test
    public void shiftTimeTest() {
        assertEquals(testHelper.getExpectedValue(), testHelper.getShifter().shiftTime());
    }

    @Data
    private static class TestHelper {

        private final TimeFrameShifter shifter;
        private final LocalDateTime expectedValue;

        TestHelper(ShiftType type, int value, TimeFrame timeFrame, LocalDateTime originalValue, LocalDateTime expectedValue) {
            this.shifter = new TimeFrameShifter(originalValue, new Shift(type, value, timeFrame));
            this.expectedValue = expectedValue;
        }

    }

}
