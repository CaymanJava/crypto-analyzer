package pro.crypto.indicator.pivot;

import pro.crypto.exception.UnknownTypeException;
import pro.crypto.model.IndicatorRequest;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public class PivotPointFactory {

    public static PivotPoints create(IndicatorRequest creationRequest) {
        PivotRequest request = (PivotRequest) creationRequest;

        if (isNull(request.getIndicatorType())) {
            throw new UnknownTypeException("Pivot Points type can not be null");
        }

        switch (request.getIndicatorType()) {
            case FLOOR_PIVOT_POINTS:
                return new FloorPivotPoints(request.getOriginalData()[0]);
            case WOODIE_PIVOT_POINTS:
                return new WoodiePivotPoints(request.getOriginalData()[0]);
            case CAMARILLA_PIVOT_POINTS:
                return new CamarillaPivotPoints(request.getOriginalData()[0]);
            case DE_MARK_PIVOT_POINTS:
                return new DeMarkPivotPoints(request.getOriginalData()[0]);
            case FIBONACCI_PIVOT_POINTS:
                return new FibonacciPivotPoints(request.getOriginalData()[0]);
            default:
                throw new UnknownTypeException(format("Unknown Pivot Points type {type: {%s}}", request.getIndicatorType()));
        }
    }

}
