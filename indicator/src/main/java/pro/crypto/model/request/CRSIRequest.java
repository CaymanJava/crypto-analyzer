package pro.crypto.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CRSIRequest {

    @NotNull
    private Tick[] originalData;

    private IndicatorType movingAverageType;

    private int simpleRsiPeriod;

    private int streakRsiPeriod;

    private int percentRankPeriod;

}
