package pro.crypto.model.analyzer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrendStrength {

    private Trend trend;

    private Strength strength;

}
