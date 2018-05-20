package pro.crypto.helper;

import pro.crypto.model.result.MAResult;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class MAResultExtractor {

    public static BigDecimal[] extract(MAResult[] result) {
        return Stream.of(result)
                .map(MAResult::getIndicatorValue)
                .toArray(BigDecimal[]::new);
    }

}
