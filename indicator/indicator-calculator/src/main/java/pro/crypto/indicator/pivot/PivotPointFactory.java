package pro.crypto.indicator.pivot;

import pro.crypto.exception.UnknownTypeException;
import pro.crypto.request.IndicatorRequest;

import static java.lang.String.format;
import static java.util.Objects.isNull;

/**
 * Use Pivot Points only with ONE_DAY time frame!
 */
public class PivotPointFactory {

    public static PivotPoints create(IndicatorRequest creationRequest) {
        PivotRequest request = (PivotRequest) creationRequest;

        if (isNull(request.getIndicatorType())) {
            throw new UnknownTypeException("Pivot Points type can not be null");
        }

        switch (request.getIndicatorType()) {
            case FLOOR_PIVOT_POINTS:
                return new FloorPivotPoints(request.getOriginalData(), request.getResultData());
            case WOODIE_PIVOT_POINTS:
                return new WoodiePivotPoints(request.getOriginalData(), request.getResultData());
            case CAMARILLA_PIVOT_POINTS:
                return new CamarillaPivotPoints(request.getOriginalData(), request.getResultData());
            case DE_MARK_PIVOT_POINTS:
                return new DeMarkPivotPoints(request.getOriginalData(), request.getResultData());
            case FIBONACCI_PIVOT_POINTS:
                return new FibonacciPivotPoints(request.getOriginalData(), request.getResultData());
            default:
                throw new UnknownTypeException(format("Unknown Pivot Points type {type: {%s}}", request.getIndicatorType()));
        }
    }

}
