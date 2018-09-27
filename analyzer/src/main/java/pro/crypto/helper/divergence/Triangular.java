package pro.crypto.helper.divergence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Triangular {

    private BigDecimal abscissaCatheter;

    private BigDecimal ordinateCatheter;

    private BigDecimal hypotenuse;

}
