package pro.crypto.indicators.pivot;

import pro.crypto.exception.UnknownTypeException;
import pro.crypto.model.request.PivotRequest;

import static java.lang.String.format;

public class PivotPointFactory {

    public static PivotPoints create(PivotRequest request) {
        switch (request.getIndicatorType()) {
            case FLOOR_PIVOT_POINTS:
                return new FloorPivotPoints(request.getOriginalData());
            case WOODIE_PIVOT_POINTS:
                return new WoodiePivotPoints(request.getOriginalData());
            case CAMARILLA_PIVOT_POINTS:
                return new CamarillaPivotPoints(request.getOriginalData());
            default:
                throw new UnknownTypeException(format("Unknown Pivot Points type {type: {%s}}", request.getIndicatorType()));
        }
    }

//    DEMARK_PIVOT_POINTS,
//    FIBONACCI_PIVOT_POINTS

}
