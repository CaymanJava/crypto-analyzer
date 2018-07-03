package pro.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BigDecimalTuple {

    private BigDecimal left;

    private BigDecimal right;

    public BigDecimalTuple add(BigDecimalTuple value) {
        left = left.add(value.getLeft());
        right = right.add(value.getRight());
        return this;
    }

    public static BigDecimalTuple zero() {
        return new BigDecimalTuple(BigDecimal.ZERO, BigDecimal.ZERO);
    }

}
