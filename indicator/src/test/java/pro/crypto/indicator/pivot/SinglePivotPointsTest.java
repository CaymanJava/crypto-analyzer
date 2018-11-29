package pro.crypto.indicator.pivot;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pro.crypto.exception.UnknownTypeException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.indicator.IndicatorAbstractTest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.*;

public class SinglePivotPointsTest extends IndicatorAbstractTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testFloorPivotPoints() {
        IndicatorResult[] expectedResult = loadExpectedResult("floor_pivot_points.json", PivotResult[].class);
        PivotResult[] actualResult = PivotPointFactory.create(buildRequest(FLOOR_PIVOT_POINTS)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testWoodiePivotPoints() {
        IndicatorResult[] expectedResult = loadExpectedResult("woodie_pivot_points.json", PivotResult[].class);
        PivotResult[] actualResult = PivotPointFactory.create(buildRequest(WOODIE_PIVOT_POINTS)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testCamarillaPivotPoints() {
        IndicatorResult[] expectedResult = loadExpectedResult("camarilla_pivot_points.json", PivotResult[].class);
        PivotResult[] actualResult = PivotPointFactory.create(buildRequest(CAMARILLA_PIVOT_POINTS)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testDeMarkPivotPoints() {
        IndicatorResult[] expectedResult = loadExpectedResult("de_mark_pivot_points.json", PivotResult[].class);
        PivotResult[] actualResult = PivotPointFactory.create(buildRequest(DE_MARK_PIVOT_POINTS)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void testFibonacciPivotPoints() {
        IndicatorResult[] expectedResult = loadExpectedResult("fibonacci_pivot_points.json", PivotResult[].class);
        PivotResult[] actualResult = PivotPointFactory.create(buildRequest(FIBONACCI_PIVOT_POINTS)).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void unknownPivotPointTypeExceptionTest() {
        expectedException.expect(UnknownTypeException.class);
        expectedException.expectMessage("Unknown Pivot Points type {type: {SIMPLE_MOVING_AVERAGE}}");
        PivotPointFactory.create(PivotRequest.builder()
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .originalData(new Tick[100])
                .build());
    }

    @Test
    public void nullOriginalDataTest() {
        expectedException.expect(WrongIncomingParametersException.class);
        expectedException.expectMessage("Incoming tick data is null {indicator: {FIBONACCI_PIVOT_POINTS}}");
        PivotPointFactory.create(PivotRequest.builder()
                .indicatorType(FIBONACCI_PIVOT_POINTS)
                .build());
    }

    @Test
    public void nullPivotPointTypeTest() {
        expectedException.expect(UnknownTypeException.class);
        expectedException.expectMessage("Pivot Points type can not be null");
        PivotPointFactory.create(PivotRequest.builder()
                .indicatorType(null)
                .originalData(new Tick[1])
                .build());
    }

    @Override
    protected PivotRequest buildRequest() {
        return PivotRequest.builder()
                .originalData(originalData)
                .build();
    }

    private IndicatorRequest buildRequest(IndicatorType pivotType) {
        PivotRequest pivotRequest = buildRequest();
        pivotRequest.setIndicatorType(pivotType);
        return pivotRequest;
    }

}
