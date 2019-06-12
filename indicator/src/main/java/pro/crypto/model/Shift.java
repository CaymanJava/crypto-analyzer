package pro.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import pro.crypto.model.tick.TimeFrame;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Shift {

    @NotNull
    private ShiftType type;

    private int value;

    private TimeFrame timeFrame;

}
