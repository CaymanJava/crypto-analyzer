package pro.crypto.indicator.tick.generator;

import pro.crypto.model.tick.Tick;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class IncreasedQuantityTickGenerator extends OneDayTickWithFullPriceGenerator {

    public IncreasedQuantityTickGenerator(LocalDateTime startDateTime) {
        super(startDateTime);
    }

    @Override
    public Tick[] generate() {
        List<Tick> ticks = Arrays.asList(super.generate());
        List<Tick> additionalTicks = generateAdditionalTicks();
        return Stream.concat(ticks.stream(), additionalTicks.stream())
                .toArray(Tick[]::new);
    }

    private List<Tick> generateAdditionalTicks() {
        return Arrays.asList(generateTick(1198.50, 1221.60, 1193.42, 1220.53, 145.0532),
                generateTick(1259.87, 1282.74, 1259.87, 1282.4399, 153.5853),
                generateTick(1239.54, 1253.61, 1229.0601, 1232.42, 170.3715),
                generateTick(1272.26, 1283.21, 1263.46, 1272.29, 89.5419),
                generateTick(1270.13, 1288.08, 1253.4399, 1287.86, 114.2959),
                generateTick(1276.76, 1312.53, 1267.76, 1309.67, 194.7072),
                generateTick(1285.54, 1307.60, 1280.66, 1292.80, 59.4445),
                generateTick(1288.24, 1320.25, 1279.46, 1320.23, 114.5847),
                generateTick(1325.08, 1330.99, 1296.54, 1298.71, 138.3779),
                generateTick(1297.33, 1331.33, 1297.17, 1331.13, 103.1518),
                generateTick(1345.72, 1346.21, 1310.63, 1315.8101, 166.7943),
                generateTick(1313.14, 1318.9301, 1279.1899, 1300.53, 73.3755),
                generateTick(1307.49, 1334.63, 1300.55, 1326.73, 58.6737),
                generateTick(1330.73, 1347.58, 1323.09, 1329.76, 139.7850),
                generateTick(1320.95, 1360.83, 1313.72, 1360.7, 198.6979),
                generateTick(1396.5699, 1420.03, 1388.6801, 1396.54, 64.6723),
                generateTick(1386.87, 1401.37, 1379.33, 1401.16, 169.8716),
                generateTick(1408.22, 1419.04, 1386.52, 1418.99, 174.9113),
                generateTick(1397.54, 1400.08, 1371.47, 1376.71, 32.7192),
                generateTick(1376.37, 1389.77, 1354.28, 1359.25, 41.7305),
                generateTick(1355.10, 1355.10, 1319.0699, 1319.21, 184.3328),
                generateTick(1328.08, 1367.97, 1328.08, 1349.5601, 146.2895),
                generateTick(1342.22, 1371.74, 1334.13, 1361.28, 79.3354),
                generateTick(1378.9399, 1411.63, 1378.9399, 1411.52, 26.9561),
                generateTick(1396.11, 1413.53, 1386.14, 1411.14, 111.6908),
                generateTick(1422.54, 1425.42, 1393.66, 1393.6899, 88.1620),
                generateTick(1387.11, 1394.9301, 1367.76, 1374.51, 106.0133),
                generateTick(1375.6899, 1419.64, 1375.41, 1419.35, 190.2682),
                generateTick(1413.13, 1468.72, 1430.08, 1467.5601, 184.2183),
                generateTick(1453.05, 1475.35, 1449.50, 1468.74, 96.0736),
                generateTick(1470.64, 1486.9399, 1461.13, 1481.96, 170.3826),
                generateTick(1473.23, 1478.73, 1441.12, 1444.41, 109.7508),
                generateTick(1463.27, 1491.45, 1462.62, 1487.9399, 197.1565),
                generateTick(1495.8101, 1497.4399, 1478.72, 1478.78, 142.5078),
                generateTick(1507.9399, 1521.4399, 1474.59, 1484.78, 97.7079),
                generateTick(1474.6899, 1474.6899, 1445.23, 1448.96, 35.7326),
                generateTick(1427.05, 1444.1801, 1412.92, 1430.35, 63.8226),
                generateTick(1444.74, 1445.95, 1410.58, 1410.75, 114.1121),
                generateTick(1395.1801, 1430.39, 1391.10, 1422.4399, 61.3234),
                generateTick(1411.40, 1411.40, 1367.0699, 1367.14, 52.1259),
                generateTick(1374.60, 1397.80, 1373.90, 1390.80, 79.2830),
                generateTick(1382.10, 1407.15, 1377.71, 1396.59, 76.3990),
                generateTick(1407.02, 1411.6899, 1388.51, 1399.55, 58.4988),
                generateTick(1387.71, 1387.71, 1362.5601, 1362.61, 13.9093));
    }

}
