package pro.crypto.indicators.pivot;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.UnknownTypeException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.model.request.PivotRequest;
import pro.crypto.model.tick.Tick;

import static pro.crypto.model.IndicatorType.FIBONACCI_PIVOT_POINTS;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;

public class SinglePivotPointsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void unknownPivotPointTypeExceptionTest() {
        expectedException.expect(UnknownTypeException.class);
        expectedException.expectMessage("Unknown Pivot Points type {type: {SIMPLE_MOVING_AVERAGE}}");
        PivotPointFactory.create(PivotRequest.builder()
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .originalData(new Tick())
                .build());
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {FIBONACCI_PIVOT_POINTS}}");
        PivotPointFactory.create(PivotRequest.builder()
                .indicatorType(FIBONACCI_PIVOT_POINTS)
                .originalData(null)
                .build());
    }

}
