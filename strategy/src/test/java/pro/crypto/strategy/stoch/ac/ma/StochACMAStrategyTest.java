package pro.crypto.strategy.stoch.ac.ma;

import org.junit.Test;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.Position.ENTRY_LONG;
import static pro.crypto.model.Position.ENTRY_SHORT;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class StochACMAStrategyTest extends StrategyBaseTest {

    @Test
    public void testStochACMAStrategy() throws Exception {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("stoch_ac_ma.json", StochACMAResult[].class);
        StochACMAResult[] actualResult = new StochACMAStrategy(buildStochACMARequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildStochACMARequest() {
        return StochACMARequest.builder()
                .originalData(originalData)
                .stochMovingAverageType(SIMPLE_MOVING_AVERAGE)
                .stochFastPeriod(14)
                .stochSlowPeriod(3)
                .stochOversoldLevel(20.0)
                .stochOverboughtLevel(80.0)
                .acSlowPeriod(34)
                .acFastPeriod(5)
                .acSmoothedPeriod(5)
                .maPeriod(30)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .positions(newHashSet(ENTRY_LONG, ENTRY_SHORT))
                .build();
    }

}
