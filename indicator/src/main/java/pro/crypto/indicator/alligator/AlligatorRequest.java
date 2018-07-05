package pro.crypto.indicator.alligator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.Tick;
import pro.crypto.model.tick.TimeFrame;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlligatorRequest {

    private Tick[] originalData;

    private int jawPeriod;

    private int jawOffset;

    private int teethPeriod;

    private int teethOffset;

    private int lipsPeriod;

    private int lipsOffset;

    private TimeFrame timeFrame;

}
