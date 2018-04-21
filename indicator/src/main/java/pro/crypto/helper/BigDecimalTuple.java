package pro.crypto.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BigDecimalTuple {

    private BigDecimal left;

    private BigDecimal right;

}
