package pro.crypto.strategy.stoch.ac.ma;

import org.junit.Test;
import pro.crypto.request.StrategyRequest;
import pro.crypto.response.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.strategy.Position.ENTRY_LONG;
import static pro.crypto.model.strategy.Position.ENTRY_SHORT;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class StochAcMaStrategyTest extends StrategyBaseTest {

    @Test
    public void testStochAcMaStrategy() {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("stoch_ac_ma.json", StochAcMaResult[].class);
        StochAcMaResult[] actualResult = new StochAcMaStrategy(buildStochAcMaRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildStochAcMaRequest() {
        return StochAcMaRequest.builder()
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
