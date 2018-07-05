package pro.crypto.indicator.kvo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KVORequest {

    private Tick[] originalData;

    private int shortPeriod;

    private int longPeriod;

    private int signalPeriod;

}