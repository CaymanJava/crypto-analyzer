package pro.crypto.model;

import lombok.Getter;
import pro.crypto.model.tick.TimeFrame;

import javax.validation.constraints.NotNull;

public class Shift {

    @Getter
    @NotNull
    private final ShiftType type;

    @Getter
    private final int value;

    @Getter
    @NotNull
    private final TimeFrame timeFrame;

    public Shift(ShiftType type, int value, TimeFrame timeFrame) {
        this.type = type;
        this.value = value;
        this.timeFrame = timeFrame;
    }

}
