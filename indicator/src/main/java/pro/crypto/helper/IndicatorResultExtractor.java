package pro.crypto.helper;

import pro.crypto.model.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class IndicatorResultExtractor {

    public static BigDecimal[] extract(SimpleIndicatorResult[] result) {
        return Stream.of(result)
                .map(SimpleIndicatorResult::getIndicatorValue)
                .toArray(BigDecimal[]::new);
    }

}
