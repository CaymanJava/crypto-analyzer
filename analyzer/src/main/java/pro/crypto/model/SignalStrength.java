package pro.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignalStrength {

    private Signal signal;

    private Strength strength;

}
