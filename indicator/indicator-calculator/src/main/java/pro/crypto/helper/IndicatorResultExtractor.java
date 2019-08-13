package pro.crypto.helper;

import pro.crypto.response.SignalLineIndicatorResult;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class IndicatorResultExtractor {

    public static BigDecimal[] extractIndicatorValues(SimpleIndicatorResult[] result) {
        return Stream.of(result)
                .map(SimpleIndicatorResult::getIndicatorValue)
                .toArray(BigDecimal[]::new);
    }

    public static BigDecimal[] extractSignalLineValues(SignalLineIndicatorResult[] result) {
        return Stream.of(result)
                .map(SignalLineIndicatorResult::getSignalLineValue)
                .toArray(BigDecimal[]::new);
    }

}
