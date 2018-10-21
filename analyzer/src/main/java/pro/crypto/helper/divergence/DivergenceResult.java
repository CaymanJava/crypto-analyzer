package pro.crypto.helper.divergence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivergenceResult {

    private DivergenceType divergenceType;

    private DivergenceClass divergenceClass;

    private int indexFrom;

    private int indexTo;

}
