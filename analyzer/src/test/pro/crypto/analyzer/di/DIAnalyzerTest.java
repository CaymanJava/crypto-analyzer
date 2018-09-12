package pro.crypto.analyzer.di;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.di.DIRequest;
import pro.crypto.indicator.di.DisparityIndex;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class DIAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testDisparityIndexAnalyzer() {
        IndicatorResult[] indicatorResults = new DisparityIndex(buildIndicatorRequest()).getResult();
        DIAnalyzerResult[] result = new DIAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
        assertEquals(result[28].getTime(), of(2018, 3, 25, 0, 0));
        assertEquals(result[28].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[35].getTime(), of(2018, 4, 1, 0, 0));
        assertEquals(result[35].getSignalStrength(), new SignalStrength(SELL, WEAK));
        assertEquals(result[41].getTime(), of(2018, 4, 7, 0, 0));
        assertEquals(result[41].getSignalStrength(), new SignalStrength(BUY, WEAK));
        assertEquals(result[49].getTime(), of(2018, 4, 15, 0, 0));
        assertEquals(result[49].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[50].getTime(), of(2018, 4, 16, 0, 0));
        assertEquals(result[50].getSignalStrength(), new SignalStrength(BUY, STRONG));
        assertEquals(result[62].getTime(), of(2018, 4, 28, 0, 0));
        assertEquals(result[62].getSignalStrength(), new SignalStrength(SELL, WEAK));
        assertEquals(result[65].getTime(), of(2018, 5, 1, 0, 0));
        assertEquals(result[65].getSignalStrength(), new SignalStrength(SELL, STRONG));
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignalStrength(), new SignalStrength(NEUTRAL, UNDEFINED));
    }

    private IndicatorRequest buildIndicatorRequest() {
        return DIRequest.builder()
                .originalData(originalData)
                .period(14)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}
