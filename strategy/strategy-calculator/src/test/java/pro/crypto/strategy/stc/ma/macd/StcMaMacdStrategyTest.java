package pro.crypto.strategy.stc.ma.macd;

import org.junit.Test;
import pro.crypto.model.strategy.Position;
import pro.crypto.request.StrategyRequest;
import pro.crypto.response.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class StcMaMacdStrategyTest extends StrategyBaseTest {

    @Test
    public void testRsiEisMaStrategy() {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("stc_ma_macd.json", StcMaMacdResult[].class);
        StcMaMacdResult[] actualResult = new StcMaMacdStrategy(buildStcMaMacdRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildStcMaMacdRequest() {
        return StcMaMacdRequest.builder()
                .originalData(originalData)
                .stcPriceType(CLOSE)
                .stcMaType(EXPONENTIAL_MOVING_AVERAGE)
                .stcPeriod(10)
                .stcShortCycle(23)
                .stcLongCycle(50)
                .stcOversoldLevel(10.0)
                .stcOverboughtLevel(90.0)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .maPeriod(25) // in original strategy period should be 100
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(8)
                .macdSlowPeriod(17)
                .macdSignalPeriod(9)
                .positions(newHashSet(Position.values()))
                .build();
    }

}
