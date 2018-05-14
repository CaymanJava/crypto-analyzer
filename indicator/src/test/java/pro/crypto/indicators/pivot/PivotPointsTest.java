package pro.crypto.indicators.pivot;

import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pro.crypto.indicators.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.PivotRequest;
import pro.crypto.model.result.PivotResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameters;
import static pro.crypto.helper.MathHelper.toBigDecimal;
import static pro.crypto.model.IndicatorType.CAMARILLA_PIVOT_POINTS;
import static pro.crypto.model.IndicatorType.FLOOR_PIVOT_POINTS;
import static pro.crypto.model.IndicatorType.WOODIE_PIVOT_POINTS;

@RunWith(Parameterized.class)
public class PivotPointsTest {

    private final PivotTestHelper testHelper;

    public PivotPointsTest(PivotTestHelper testHelper) {
        this.testHelper = testHelper;
    }

    @Data
    private static class PivotTestHelper {

        private final static Tick[] ORIGINAL_DATA = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
        private final PivotResult actualResult;
        private final PivotResult expectedResult;

        PivotTestHelper(int position, IndicatorType type, BigDecimal pivot,
                        BigDecimal firstResistance, BigDecimal secondResistance, BigDecimal thirdResistance, BigDecimal fourthResistance,
                        BigDecimal firstSupport, BigDecimal secondSupport, BigDecimal thirdSupport, BigDecimal fourthSupport) {
            this.actualResult = PivotPointFactory.create(new PivotRequest(ORIGINAL_DATA[position], type)).getResult()[0];
            this.expectedResult = new PivotResult(ORIGINAL_DATA[position].getTickTime(), pivot,
                    firstResistance, secondResistance, thirdResistance, fourthResistance,
                    firstSupport, secondSupport, thirdSupport, fourthSupport);
        }

    }

    @Test
    public void floorPivotPointsTest() throws Exception {
        assertEquals(testHelper.getActualResult(), testHelper.getExpectedResult());
    }

    @Parameters
    public static Collection<Object> data() {
        return Arrays.asList(
                buildFloorPivotTestHelper(0, 1276.58,
                        1289.93, 1316.02, 1329.37,
                        1250.49, 1237.14, 1211.05),
                buildFloorPivotTestHelper(5, 1314.0167,
                        1328.5034, 1336.9168, 1351.4035,
                        1305.6033, 1291.1166, 1282.7032),
                buildFloorPivotTestHelper(20, 1196.1466666667,
                        1231.5833333334, 1249.4466666667, 1284.8833333334,
                        1178.2833333334, 1142.8466666667, 1124.9833333334),
                buildWoodiePivotTestHelper(3, 1293.7225,
                        1307.175, 1317.4725,
                        1283.425, 1269.9725),
                buildWoodiePivotTestHelper(8, 1286.4375,
                        1302.285, 1308.2075,
                        1280.515, 1264.6675),
                buildWoodiePivotTestHelper(23, 1147.705,
                        1160.14, 1188.185,
                        1119.66, 1107.225),
                buildCamarillaPivotTestHelper(25, 1127.66,
                        1132.3715833345, 1135.5331666655, 1138.69475, 1148.1795,
                        1126.0484166655, 1122.8868333345, 1119.72525, 1110.2405),
                buildCamarillaPivotTestHelper(32, 1272.9866666667,
                        1274.1004166673, 1275.9108333327, 1277.72125, 1283.1525,
                        1270.4795833327, 1268.6691666673, 1266.85875, 1261.4275),
                buildCamarillaPivotTestHelper(41, 1320.6366666667,
                        1329.8540000011, 1332.9779999989, 1336.102, 1345.474,
                        1323.6059999989, 1320.4820000011, 1317.358, 1307.986)
        );
    }

    private static PivotTestHelper buildFloorPivotTestHelper(int position, Double pivot,
                                                             Double firstResistance, Double secondResistance, Double thirdResistance,
                                                             Double firstSupport, Double secondSupport, Double thirdSupport) {
        return buildPivotTestHelper(position, FLOOR_PIVOT_POINTS, pivot, firstResistance, secondResistance, thirdResistance, null,
                firstSupport, secondSupport, thirdSupport, null);
    }

    private static PivotTestHelper buildWoodiePivotTestHelper(int position, Double pivot,
                                                              Double firstResistance, Double secondResistance,
                                                              Double firstSupport, Double secondSupport) {
        return buildPivotTestHelper(position, WOODIE_PIVOT_POINTS, pivot, firstResistance, secondResistance, null, null,
                firstSupport, secondSupport, null, null);
    }

    private static PivotTestHelper buildCamarillaPivotTestHelper(int position, Double pivot,
                                                                 Double firstResistance, Double secondResistance,
                                                                 Double thirdResistance, Double fourthResistance,
                                                                 Double firstSupport, Double secondSupport,
                                                                 Double thirdSupport, Double fourthSupport) {
        return buildPivotTestHelper(position, CAMARILLA_PIVOT_POINTS, pivot, firstResistance, secondResistance, thirdResistance, fourthResistance,
                firstSupport, secondSupport, thirdSupport, fourthSupport);
    }

    private static PivotTestHelper buildPivotTestHelper(int position, IndicatorType type, Double pivot, Double firstResistance, Double secondResistance, Double thirdResistance, Double fourthResistance, Double firstSupport, Double secondSupport, Double thirdSupport, Double fourthSupport) {
        return new PivotTestHelper(position, type, toBigDecimal(pivot),
                toBigDecimal(firstResistance), toBigDecimal(secondResistance), toBigDecimal(thirdResistance), toBigDecimal(fourthResistance),
                toBigDecimal(firstSupport), toBigDecimal(secondSupport), toBigDecimal(thirdSupport), toBigDecimal(fourthSupport));
    }

}