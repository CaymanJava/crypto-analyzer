package pro.crypto.helper;

import pro.crypto.model.indicator.Shift;

import java.time.LocalDateTime;
import java.util.function.Function;

public class TimeFrameShifter {

    private final LocalDateTime originalDateTime;
    private final Shift shift;

    public TimeFrameShifter(LocalDateTime originalDateTime, Shift shift) {
        this.originalDateTime = originalDateTime;
        this.shift = shift;
    }

    public LocalDateTime shiftTime() {
        switch (shift.getType()) {
            case RIGHT:
                return shiftTimeToTheRight();
            case LEFT:
                return shiftTimeToTheLeft();
            default:
                return originalDateTime;
        }
    }

    private LocalDateTime shiftTimeToTheRight() {
        switch (shift.getTimeFrame()) {
            case FIVE_MIN:
                return shiftNTimes(original -> original.plusMinutes(5));
            case FIFTEEN_MIN:
                return shiftNTimes(original -> original.plusMinutes(15));
            case THIRTY_MIN:
                return shiftNTimes(original -> original.plusMinutes(30));
            case ONE_HOUR:
                return shiftNTimes(original -> original.plusHours(1));
            case FOUR_HOURS:
                return shiftNTimes(original -> original.plusHours(4));
            case ONE_DAY:
                return shiftNTimes(original -> original.plusDays(1));
            default:
                return originalDateTime;
        }
    }

    private LocalDateTime shiftTimeToTheLeft() {
        switch (shift.getTimeFrame()) {
            case FIVE_MIN:
                return shiftNTimes(original -> original.minusMinutes(5));
            case FIFTEEN_MIN:
                return shiftNTimes(original -> original.minusMinutes(15));
            case THIRTY_MIN:
                return shiftNTimes(original -> original.minusMinutes(30));
            case ONE_HOUR:
                return shiftNTimes(original -> original.minusHours(1));
            case FOUR_HOURS:
                return shiftNTimes(original -> original.minusHours(4));
            case ONE_DAY:
                return shiftNTimes(original -> original.minusDays(1));
            default:
                return originalDateTime;
        }
    }

    private LocalDateTime shiftNTimes(Function<LocalDateTime, LocalDateTime> shiftFunction) {
        LocalDateTime finalResult = originalDateTime;
        for (int i = 0; i < shift.getValue(); i++) {
            finalResult = shiftFunction.apply(finalResult);
        }
        return finalResult;
    }

}
