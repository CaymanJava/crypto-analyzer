package pro.crypto.helper;

import pro.crypto.model.IndicatorType;

import static com.google.common.collect.Sets.newHashSet;
import static pro.crypto.model.IndicatorType.CAMARILLA_PIVOT_POINTS;
import static pro.crypto.model.IndicatorType.DE_MARK_PIVOT_POINTS;
import static pro.crypto.model.IndicatorType.DISPLACED_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.DOUBLE_EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.FIBONACCI_PIVOT_POINTS;
import static pro.crypto.model.IndicatorType.FLOOR_PIVOT_POINTS;
import static pro.crypto.model.IndicatorType.HULL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.KAUFMAN_ADAPTIVE_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.MODIFIED_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.SMOOTHED_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.TIME_SERIES_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.TRIANGULAR_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.TRIPLE_EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.VARIABLE_INDEX_DYNAMIC_AVERAGE;
import static pro.crypto.model.IndicatorType.WEIGHTED_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.WELLES_WILDERS_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.WOODIE_PIVOT_POINTS;

public class IndicatorTypeChecker {

    public static boolean isMovingAverage(IndicatorType indicatorType) {
        return newHashSet(DISPLACED_MOVING_AVERAGE, EXPONENTIAL_MOVING_AVERAGE, HULL_MOVING_AVERAGE,
                SIMPLE_MOVING_AVERAGE, SMOOTHED_MOVING_AVERAGE, WEIGHTED_MOVING_AVERAGE,
                MODIFIED_MOVING_AVERAGE, DOUBLE_EXPONENTIAL_MOVING_AVERAGE, KAUFMAN_ADAPTIVE_MOVING_AVERAGE,
                VARIABLE_INDEX_DYNAMIC_AVERAGE, TRIANGULAR_MOVING_AVERAGE, WELLES_WILDERS_MOVING_AVERAGE,
                TIME_SERIES_MOVING_AVERAGE, TRIPLE_EXPONENTIAL_MOVING_AVERAGE).contains(indicatorType);
    }

    public static boolean isPivotPoint(IndicatorType indicatorType) {
        return newHashSet(FLOOR_PIVOT_POINTS, WOODIE_PIVOT_POINTS, CAMARILLA_PIVOT_POINTS,
                DE_MARK_PIVOT_POINTS, FIBONACCI_PIVOT_POINTS).contains(indicatorType);
    }

}
