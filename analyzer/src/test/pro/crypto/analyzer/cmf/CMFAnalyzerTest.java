package pro.crypto.analyzer.cmf;

import org.junit.Before;
import org.junit.Test;
import pro.crypto.indicator.cmf.CMFRequest;
import pro.crypto.indicator.cmf.ChaikinMoneyFlow;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Trend.*;

public class CMFAnalyzerTest {

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @Test
    public void testChaikinMoneyFlowAnalyzer() {
        IndicatorResult[] indicatorResults = new ChaikinMoneyFlow(buildIndicatorRequest()).getResult();
        CMFAnalyzerResult[] result = new CMFAnalyzer(buildAnalyzerRequest(indicatorResults)).getResult();
        assertTrue(result.length == originalData.length);
        assertEquals(result[0].getTime(), of(2018, 2, 25, 0, 0));
        assertEquals(result[0].getSignal(), NEUTRAL);
        assertEquals(result[0].getTrend(), UNDEFINED);
        assertEquals(result[20].getTime(), of(2018, 3, 17, 0, 0));
        assertEquals(result[20].getSignal(), NEUTRAL);
        assertEquals(result[20].getTrend(), DOWNTREND);
        assertEquals(result[34].getTime(), of(2018, 3, 31, 0, 0));
        assertEquals(result[34].getSignal(), NEUTRAL);
        assertEquals(result[34].getTrend(), CONSOLIDATION);
        assertEquals(result[36].getTime(), of(2018, 4, 2, 0, 0));
        assertEquals(result[36].getSignal(), BUY);
        assertEquals(result[36].getTrend(), UPTREND);
        assertEquals(result[39].getTime(), of(2018, 4, 5, 0, 0));
        assertEquals(result[39].getSignal(), SELL);
        assertEquals(result[39].getTrend(), CONSOLIDATION);
        assertEquals(result[43].getTime(), of(2018, 4, 9, 0, 0));
        assertEquals(result[43].getSignal(), BUY);
        assertEquals(result[43].getTrend(), UPTREND);
        assertEquals(result[56].getTime(), of(2018, 4, 22, 0, 0));
        assertEquals(result[56].getSignal(), BUY);
        assertEquals(result[56].getTrend(), UPTREND);
        assertEquals(result[67].getTime(), of(2018, 5, 3, 0, 0));
        assertEquals(result[67].getSignal(), BUY);
        assertEquals(result[67].getTrend(), CONSOLIDATION);
        assertEquals(result[69].getTime(), of(2018, 5, 5, 0, 0));
        assertEquals(result[69].getSignal(), BUY);
        assertEquals(result[69].getTrend(), CONSOLIDATION);
        assertEquals(result[70].getTime(), of(2018, 5, 6, 0, 0));
        assertEquals(result[70].getSignal(), BUY);
        assertEquals(result[70].getTrend(), UPTREND);
        assertEquals(result[72].getTime(), of(2018, 5, 8, 0, 0));
        assertEquals(result[72].getSignal(), NEUTRAL);
        assertEquals(result[72].getTrend(), UPTREND);
    }

    private IndicatorRequest buildIndicatorRequest() {
        return CMFRequest.builder()
                .originalData(originalData)
                .period(21)
                .build();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return AnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(indicatorResults)
                .build();
    }

}