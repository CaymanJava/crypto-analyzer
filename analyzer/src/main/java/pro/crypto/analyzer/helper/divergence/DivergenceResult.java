package pro.crypto.analyzer.helper.divergence;

import lombok.*;

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
