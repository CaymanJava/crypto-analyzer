package pro.crypto.strategy.stoch.cci;

import org.junit.Test;
import pro.crypto.request.StrategyRequest;
import pro.crypto.response.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.strategy.Position.ENTRY_LONG;
import static pro.crypto.model.strategy.Position.ENTRY_SHORT;

public class StochCciStrategyTest extends StrategyBaseTest {

    @Test
    public void testStochCciStrategy() {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("stoch_cci.json", StochCciResult[].class);
        StochCciResult[] actualResult = new StochCciStrategy(buildStochCciRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildStochCciRequest() {
        return StochCciRequest.builder()
                .originalData(originalData)
                .stochMovingAverageType(SIMPLE_MOVING_AVERAGE)
                .stochFastPeriod(20)
                .stochSlowPeriod(3)
                .stochOversoldLevel(20.0)
                .stochOverboughtLevel(80.0)
                .cciPeriod(20)
                .cciOversoldLevel(-100.0)
                .cciOverboughtLevel(100.0)
                .positions(newHashSet(ENTRY_LONG, ENTRY_SHORT))
                .build();
    }

}
