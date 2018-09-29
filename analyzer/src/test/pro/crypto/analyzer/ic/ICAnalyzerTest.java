package pro.crypto.analyzer.ic;

import org.junit.Test;
import pro.crypto.analyzer.IncreasedQuantityAnalyzerAbstractTest;
import pro.crypto.indicator.ic.ICRequest;
import pro.crypto.indicator.ic.IchimokuClouds;
import pro.crypto.model.*;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.analyzer.ic.ICDataGenerator.*;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.NORMAL;
import static pro.crypto.model.Strength.STRONG;
import static pro.crypto.model.Strength.WEAK;
import static pro.crypto.model.Trend.*;

public class ICAnalyzerTest extends IncreasedQuantityAnalyzerAbstractTest {

    @Test
    public void testIchimokuCloudsAnalyzer() {
        IndicatorResult[] indicatorResults = new IchimokuClouds(buildIndicatorRequest()).getResult();
        ICAnalyzerResult[] result = new ICAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[0].getTrendStrength(), new TrendStrength(UNDEFINED, Strength.UNDEFINED));
        assertEquals(result[34].getTime(), of(2018, 3, 31, 0, 0));
        assertEquals(result[34].getSignalStrength(), new SignalStrength(BUY, WEAK));
        assertEquals(result[34].getTrendStrength(), new TrendStrength(UNDEFINED, Strength.UNDEFINED));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignalStrength(), new SignalStrength(SELL, WEAK));
        assertEquals(result[72].getTrendStrength(), new TrendStrength(UNDEFINED, Strength.UNDEFINED));
        assertEquals(result[77].getTime(), of(2018, 5, 13, 0, 0));
        assertEquals(result[77].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[77].getTrendStrength(), new TrendStrength(CONSOLIDATION, NORMAL));
        assertEquals(result[89].getTime(), of(2018, 5, 25, 0, 0));
        assertEquals(result[89].getSignalStrength(), new SignalStrength(BUY, WEAK));
        assertEquals(result[89].getTrendStrength(), new TrendStrength(CONSOLIDATION, NORMAL));
        assertEquals(result[99].getTime(), of(2018, 6, 4, 0, 0));
        assertEquals(result[99].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[99].getTrendStrength(), new TrendStrength(UPTREND, STRONG));
        assertEquals(result[105].getTime(), of(2018, 6, 10, 0, 0));
        assertEquals(result[105].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[105].getTrendStrength(), new TrendStrength(UPTREND, WEAK));
        assertEquals(result[116].getTime(), of(2018, 6, 21, 0, 0));
        assertEquals(result[116].getSignalStrength(), new SignalStrength(SELL, WEAK));
        assertEquals(result[116].getTrendStrength(), new TrendStrength(UPTREND, WEAK));
    }

    @Test
    public void testTenkanKijunCross() {
        ICAnalyzerResult[] result = new ICAnalyzer(generateDataForTenkanKijunCrossTest()).getResult();
        assertTrue(result.length == 7);
        assertEquals(result[0].getTime(), of(2018, 1, 1, 0, 0));
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[0].getTrendStrength(), new TrendStrength(UPTREND, STRONG));
        assertEquals(result[1].getTime(), of(2018, 1, 2, 0, 0));
        assertEquals(result[1].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[1].getTrendStrength(), new TrendStrength(UPTREND, STRONG));
        assertEquals(result[2].getTime(), of(2018, 1, 3, 0, 0));
        assertEquals(result[2].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[2].getTrendStrength(), new TrendStrength(UPTREND, STRONG));
        assertEquals(result[3].getTime(), of(2018, 1, 4, 0, 0));
        assertEquals(result[3].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[3].getTrendStrength(), new TrendStrength(DOWNTREND, STRONG));
        assertEquals(result[4].getTime(), of(2018, 1, 5, 0, 0));
        assertEquals(result[4].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[4].getTrendStrength(), new TrendStrength(DOWNTREND, STRONG));
        assertEquals(result[5].getTime(), of(2018, 1, 6, 0, 0));
        assertEquals(result[5].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[5].getTrendStrength(), new TrendStrength(CONSOLIDATION, NORMAL));
        assertEquals(result[6].getTime(), of(2018, 1, 7, 0, 0));
        assertEquals(result[6].getSignalStrength(), new SignalStrength(BUY, WEAK));
        assertEquals(result[6].getTrendStrength(), new TrendStrength(CONSOLIDATION, NORMAL));
    }

    @Test
    public void testPriceKijunCross() {
        ICAnalyzerResult[] result = new ICAnalyzer(generateDataForPriceKijunCrossSignals()).getResult();
        assertTrue(result.length == 7);
        assertEquals(result[0].getTime(), of(2018, 1, 1, 0, 0));
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[0].getTrendStrength(), new TrendStrength(UPTREND, STRONG));
        assertEquals(result[1].getTime(), of(2018, 1, 2, 0, 0));
        assertEquals(result[1].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[1].getTrendStrength(), new TrendStrength(UPTREND, STRONG));
        assertEquals(result[2].getTime(), of(2018, 1, 3, 0, 0));
        assertEquals(result[2].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[2].getTrendStrength(), new TrendStrength(UPTREND, STRONG));
        assertEquals(result[3].getTime(), of(2018, 1, 4, 0, 0));
        assertEquals(result[3].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[3].getTrendStrength(), new TrendStrength(DOWNTREND, STRONG));
        assertEquals(result[4].getTime(), of(2018, 1, 5, 0, 0));
        assertEquals(result[4].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[4].getTrendStrength(), new TrendStrength(DOWNTREND, STRONG));
        assertEquals(result[5].getTime(), of(2018, 1, 6, 0, 0));
        assertEquals(result[5].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[5].getTrendStrength(), new TrendStrength(CONSOLIDATION, NORMAL));
        assertEquals(result[6].getTime(), of(2018, 1, 7, 0, 0));
        assertEquals(result[6].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[6].getTrendStrength(), new TrendStrength(CONSOLIDATION, NORMAL));
    }

    @Test
    public void testPriceCloudCross() {
        ICAnalyzerResult[] result = new ICAnalyzer(generateDataForPriceCloudCross()).getResult();
        assertTrue(result.length == 7);
        assertEquals(result[0].getTime(), of(2018, 1, 1, 0, 0));
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[0].getTrendStrength(), new TrendStrength(CONSOLIDATION, NORMAL));
        assertEquals(result[1].getTime(), of(2018, 1, 2, 0, 0));
        assertEquals(result[1].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[1].getTrendStrength(), new TrendStrength(CONSOLIDATION, NORMAL));
        assertEquals(result[2].getTime(), of(2018, 1, 3, 0, 0));
        assertEquals(result[2].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[2].getTrendStrength(), new TrendStrength(UPTREND, WEAK));
        assertEquals(result[3].getTime(), of(2018, 1, 4, 0, 0));
        assertEquals(result[3].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[3].getTrendStrength(), new TrendStrength(UPTREND, STRONG));
        assertEquals(result[4].getTime(), of(2018, 1, 5, 0, 0));
        assertEquals(result[4].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[4].getTrendStrength(), new TrendStrength(CONSOLIDATION, NORMAL));
        assertEquals(result[5].getTime(), of(2018, 1, 6, 0, 0));
        assertEquals(result[5].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[5].getTrendStrength(), new TrendStrength(DOWNTREND, WEAK));
        assertEquals(result[6].getTime(), of(2018, 1, 7, 0, 0));
        assertEquals(result[6].getSignalStrength(), new SignalStrength(NEUTRAL, Strength.UNDEFINED));
        assertEquals(result[6].getTrendStrength(), new TrendStrength(CONSOLIDATION, NORMAL));
    }

    @Override
    protected IndicatorRequest buildIndicatorRequest() {
        return ICRequest.builder()
                .originalData(originalData)
                .conversionLinePeriod(9)
                .baseLinePeriod(26)
                .leadingSpanPeriod(52)
                .displaced(26)
                .build();
    }

}
