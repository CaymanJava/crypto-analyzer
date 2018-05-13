package pro.crypto.indicators.pivot;

import pro.crypto.exception.UnknownTypeException;
import pro.crypto.model.request.PivotRequest;

import static java.lang.String.format;

public class PivotPointFactory {

    public static PivotPoints create(PivotRequest request) {
        switch (request.getIndicatorType()) {
            case FLOOR_PIVOT_POINTS:
                    return new FloorPivotPoints(request.getOriginalData());
            default:
                throw new UnknownTypeException(format("Unknown Pivot Points type {type: {%s}}", request.getIndicatorType()));
        }
    }

//    WOODIE_PIVOT_POINTS,
//    CAMARILLA_PIVOT_POINTS,
//    DEMARK_PIVOT_POINTS,
//    FIBONACCI_PIVOT_POINTS

}
