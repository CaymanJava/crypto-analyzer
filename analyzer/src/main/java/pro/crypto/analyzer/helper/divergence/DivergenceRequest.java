package pro.crypto.analyzer.helper.divergence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivergenceRequest {

    private Tick[] originalData;

    private BigDecimal[] indicatorValues;

}
