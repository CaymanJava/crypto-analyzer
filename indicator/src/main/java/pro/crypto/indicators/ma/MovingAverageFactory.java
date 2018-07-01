package pro.crypto.indicators.ma;

import pro.crypto.exception.UnknownTypeException;
import pro.crypto.model.request.MARequest;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public class MovingAverageFactory {

    public static MovingAverage create(MARequest request) {
        if (isNull(request.getIndicatorType())) {
            throw new UnknownTypeException(format("Unknown moving average type {type: {%s}}", request.getIndicatorType()));
        }

        switch (request.getIndicatorType()) {
            case DISPLACED_MOVING_AVERAGE:
                return new DisplacedMovingAverage(request.getOriginalIndicatorType(), request.getPriceType(),
                        request.getOriginalData(), request.getPeriod(), request.getAlphaCoefficient(), request.getShift());
            case EXPONENTIAL_MOVING_AVERAGE:
                return new ExponentialMovingAverage(request.getOriginalData(), request.getPeriod(),
                        request.getPriceType(), request.getAlphaCoefficient());
            case HULL_MOVING_AVERAGE:
                return new HullMovingAverage(request.getOriginalData(), request.getPeriod(), request.getPriceType());
            case SIMPLE_MOVING_AVERAGE:
                return new SimpleMovingAverage(request.getOriginalData(), request.getPeriod(), request.getPriceType());
            case SMOOTHED_MOVING_AVERAGE:
                return new SmoothedMovingAverage(request.getOriginalData(), request.getPeriod(), request.getPriceType());
            case WEIGHTED_MOVING_AVERAGE:
                return new WeightedMovingAverage(request.getOriginalData(), request.getPeriod(), request.getPriceType());
            case MODIFIED_MOVING_AVERAGE:
                return new ModifiedMovingAverage(request.getOriginalData(), request.getPeriod(), request.getPriceType());
            case DOUBLE_EXPONENTIAL_MOVING_AVERAGE:
                return new DoubleExponentialMovingAverage(request.getOriginalData(), request.getPeriod(),
                        request.getPriceType(), request.getAlphaCoefficient());
            case KAUFMAN_ADAPTIVE_MOVING_AVERAGE:
                return new KaufmanAdaptiveMovingAverage(request.getOriginalData(), request.getPeriod(), request.getPriceType());
            case VARIABLE_INDEX_DYNAMIC_AVERAGE:
                return new VariableIndexDynamicAverage(request.getOriginalData(), request.getPeriod(), request.getPriceType());
            case TRIANGULAR_MOVING_AVERAGE:
                return new TriangularMovingAverage(request.getOriginalData(), request.getPeriod(), request.getPriceType());
            case WELLES_WILDERS_MOVING_AVERAGE:
                return new WellesWildersMovingAverage(request.getOriginalData(), request.getPeriod(), request.getPriceType());
            default:
                throw new UnknownTypeException(format("Unknown moving average type {type: {%s}}", request.getIndicatorType()));
        }
    }

}
