package pro.crypto.indicator.adl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.Tick;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ADLRequest {

    private Tick[] originalData;

}
