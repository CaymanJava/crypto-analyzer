package pro.crypto.helper;

import pro.crypto.model.IndicatorType;

import static pro.crypto.model.IndicatorType.*;

public class IndicatorTypeChecker {

    public static boolean isMovingAverageType(IndicatorType originalIndicatorType) {
        return originalIndicatorType == SIMPLE_MOVING_AVERAGE ||
                originalIndicatorType == EXPONENTIAL_MOVING_AVERAGE ||
                originalIndicatorType == WEIGHTED_MOVING_AVERAGE ||
                originalIndicatorType == SMOOTHED_MOVING_AVERAGE ||
                originalIndicatorType == HULL_MOVING_AVERAGE;
    }

}
