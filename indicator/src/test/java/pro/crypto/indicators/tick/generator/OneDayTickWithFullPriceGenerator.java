package pro.crypto.indicators.tick.generator;

import pro.crypto.model.tick.Tick;

import java.time.LocalDateTime;

public class OneDayTickWithFullPriceGenerator extends AbstractGenerator {

    public OneDayTickWithFullPriceGenerator(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    @Override
    public Tick[] generate() {
        return new Tick[] {
                generateTick(1302.67, 1302.67, 1263.23, 1263.84),
                generateTick(1268.65, 1294.65, 1261, 1292.3101),
                generateTick(1274.76, 1274.76, 1251, 1251),
                generateTick(1280.27, 1304.02, 1280.27, 1295.3),
                generateTick(1286.75, 1310.33, 1270.73, 1304.60),
                generateTick(1306.13, 1322.4301, 1299.53, 1320.09),
                generateTick(1328.35, 1347.27, 1314.96, 1315.45),
                generateTick(1305.72, 1305.72, 1279.09, 1279.68),
                generateTick(1272.9399, 1292.36, 1270.59, 1291.4),
                generateTick(1286.85, 1292.73, 1267.6899, 1275.88),
                generateTick(1292.91, 1298.5, 1258.85, 1259.9399),
                generateTick(1244.52, 1263.90, 1233.08, 1252.13),
                generateTick(1233.9399, 1242.91, 1216.1899, 1216.45),
                generateTick(1229.47, 1232.96, 1216.24, 1221.09),
                generateTick(1209.13, 1209.72, 1177.41, 1184.9301),
                generateTick(1170.95, 1200.45, 1169.04, 1182.17),
                generateTick(1195.60, 1227.23, 1184.12, 1222.29),
                generateTick(1231.85, 1239.62, 1206.91, 1221.61),
                generateTick(1213.77, 1235.08, 1198.12, 1199.16),
                generateTick(1187.48, 1190.74, 1160.0699, 1172.0601),
                generateTick(1180.26, 1214.01, 1160.71, 1213.72),
                generateTick(1208.03, 1222.72, 1183.76, 1187.3101),
                generateTick(1183.11, 1197.96, 1164.51, 1165.5601),
                generateTick(1174.58, 1175.75, 1135.27, 1139.9),
                generateTick(1135.79, 1145.79, 1113.36, 1119.4),
                generateTick(1129.8199, 1144.13, 1109.64, 1129.21),
                generateTick(1117.14, 1135.89, 1112.08, 1114.11),
                generateTick(1116.76, 1165.83, 1108.49, 1163.37),
                generateTick(1179.90, 1220.12, 1179.90, 1210.47),
                generateTick(1198.50, 1221.60, 1193.42, 1220.53),
                generateTick(1259.87, 1282.74, 1259.87, 1282.4399),
                generateTick(1239.54, 1253.61, 1229.0601, 1232.42),
                generateTick(1272.26, 1283.21, 1263.46, 1272.29),
                generateTick(1270.13, 1288.08, 1253.4399, 1287.86),
                generateTick(1276.76, 1312.53, 1267.76, 1309.67),
                generateTick(1285.54, 1307.60, 1280.66, 1292.80),
                generateTick(1288.24, 1320.25, 1279.46, 1320.23),
                generateTick(1325.08, 1330.99, 1296.54, 1298.71),
                generateTick(1297.33, 1331.33, 1297.17, 1331.13),
                generateTick(1345.72, 1346.21, 1310.63, 1315.8101),
                generateTick(1313.14, 1318.9301, 1279.1899, 1300.53),
                generateTick(1307.49, 1334.63, 1300.55, 1326.73),
                generateTick(1330.73, 1347.58, 1323.09, 1329.76),
                generateTick(1320.95, 1360.83, 1313.72, 1360.7),
                generateTick(1396.5699, 1420.03, 1388.6801, 1396.54),
                generateTick(1386.87, 1401.37, 1379.33, 1401.16),
                generateTick(1408.22, 1419.04, 1386.52, 1418.99),
                generateTick(1397.54, 1400.08, 1371.47, 1376.71),
                generateTick(1376.37, 1389.77, 1354.28, 1359.25),
                generateTick(1355.10, 1355.10, 1319.0699, 1319.21),
                generateTick(1328.08, 1367.97, 1328.08, 1349.5601),
                generateTick(1342.22, 1371.74, 1334.13, 1361.28),
                generateTick(1378.9399, 1411.63, 1378.9399, 1411.52),
                generateTick(1396.11, 1413.53, 1386.14, 1411.14),
                generateTick(1422.54, 1425.42, 1393.66, 1393.6899),
                generateTick(1387.11, 1394.9301, 1367.76, 1374.51),
                generateTick(1375.6899, 1419.64, 1375.41, 1419.35),
                generateTick(1413.13, 1468.72, 1430.08, 1467.5601),
                generateTick(1453.05, 1475.35, 1449.50, 1468.74),
                generateTick(1470.64, 1486.9399, 1461.13, 1481.96),
                generateTick(1473.23, 1478.73, 1441.12, 1444.41),
                generateTick(1463.27, 1491.45, 1462.62, 1487.9399),
                generateTick(1495.8101, 1497.4399, 1478.72, 1478.78),
                generateTick(1507.9399, 1521.4399, 1474.59, 1484.78),
                generateTick(1474.6899, 1474.6899, 1445.23, 1448.96),
                generateTick(1427.05, 1444.1801, 1412.92, 1430.35),
                generateTick(1444.74, 1445.95, 1410.58, 1410.75),
                generateTick(1395.1801, 1430.39, 1391.10, 1422.4399),
                generateTick(1411.40, 1411.40, 1367.0699, 1367.14),
                generateTick(1374.60, 1397.80, 1373.90, 1390.80),
                generateTick(1382.10, 1407.15, 1377.71, 1396.59),
                generateTick(1407.02, 1411.6899, 1388.51, 1399.55),
                generateTick(1387.71, 1387.71, 1362.5601, 1362.61)
        };
    }

    private Tick generateTick(double open, double high, double low, double close) {
        Tick tick = generateFullTick(open, high, low, close);
        plusOneDay();
        return tick;
    }

}
