package pro.crypto.helper;

import pro.crypto.model.strategy.StrategyType;

import static com.google.common.collect.Sets.newHashSet;
import static pro.crypto.model.strategy.StrategyType.PIVOT_RSI_MACD_MA;

public class StrategyTypeChecker {

    public static boolean withPivotPoints(StrategyType type) {
        return newHashSet(PIVOT_RSI_MACD_MA).contains(type);
    }

}
