package pro.crypto.model.analyzer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignalStrength {

    private Signal signal;

    private Strength strength;

}
