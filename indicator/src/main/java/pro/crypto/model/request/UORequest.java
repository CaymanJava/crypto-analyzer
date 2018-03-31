package pro.crypto.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.Tick;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UORequest {

    @NotNull
    private Tick[] originalData;

    private int shortPeriod;

    private int middlePeriod;

    private int longPeriod;

}
