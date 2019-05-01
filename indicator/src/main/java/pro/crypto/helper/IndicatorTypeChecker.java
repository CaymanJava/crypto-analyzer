package pro.crypto.helper;

import pro.crypto.model.IndicatorType;

public class IndicatorTypeChecker {

    public static boolean isMovingAverageType(IndicatorType originalIndicatorType) {
        switch (originalIndicatorType) {
            case DISPLACED_MOVING_AVERAGE:
            case EXPONENTIAL_MOVING_AVERAGE:
            case HULL_MOVING_AVERAGE:
            case SIMPLE_MOVING_AVERAGE:
            case SMOOTHED_MOVING_AVERAGE:
            case WEIGHTED_MOVING_AVERAGE:
            case MODIFIED_MOVING_AVERAGE:
            case DOUBLE_EXPONENTIAL_MOVING_AVERAGE:
            case KAUFMAN_ADAPTIVE_MOVING_AVERAGE:
            case VARIABLE_INDEX_DYNAMIC_AVERAGE:
            case TRIANGULAR_MOVING_AVERAGE:
            case WELLES_WILDERS_MOVING_AVERAGE:
            case TIME_SERIES_MOVING_AVERAGE:
            case TRIPLE_EXPONENTIAL_MOVING_AVERAGE:
                return true;
            default:
                return false;
        }
    }

}
